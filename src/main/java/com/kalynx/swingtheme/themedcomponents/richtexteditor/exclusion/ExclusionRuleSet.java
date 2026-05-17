package com.kalynx.swingtheme.themedcomponents.richtexteditor.exclusion;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;

import javax.swing.text.html.HTML;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable collection of {@link ExclusionRule}s evaluated together to decide
 * whether a command may execute.
 *
 * <p>The set is consulted by {@code AbstractEditorCommand#isEnabled} so every
 * command entry point (toolbar buttons, key bindings, programmatic
 * invocation) gets the same gating for free.</p>
 *
 * <p>Build instances via {@link #builder()} or use {@link #empty()} when no
 * rules apply.</p>
 */
public final class ExclusionRuleSet {

    private static final ExclusionRuleSet EMPTY = new ExclusionRuleSet(List.of());

    private final List<ExclusionRule> rules;

    private ExclusionRuleSet(List<ExclusionRule> rules) {
        this.rules = List.copyOf(rules);
    }

    /**
     * @return a shared empty rule set that always allows execution
     */
    public static ExclusionRuleSet empty() {
        return EMPTY;
    }

    /**
     * @return a new {@link Builder} for assembling a rule set fluently
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return {@code true} when no rule in the set blocks execution
     */
    public boolean isAllowed(EditorCommandContext context) {
        return firstBlockingReason(context) == null;
    }

    /**
     * @return the reason from the first rule that blocks execution, or
     *         {@code null} when the command is allowed
     */
    public String firstBlockingReason(EditorCommandContext context) {
        for (ExclusionRule rule : rules) {
            String reason = rule.evaluate(context);
            if (reason != null && !reason.isEmpty()) {
                return reason;
            }
        }
        return null;
    }

    /**
     * Fluent builder for {@link ExclusionRuleSet}. Convenience methods mirror
     * the factory in {@link ExclusionRules} so simple rule sets can be built
     * without separate imports.
     */
    public static final class Builder {

        private final List<ExclusionRule> rules = new ArrayList<>();

        private Builder() {
        }

        /**
         * Adds a custom rule to the set.
         */
        public Builder add(ExclusionRule rule) {
            if (rule != null) {
                rules.add(rule);
            }
            return this;
        }

        /**
         * Shortcut for {@link ExclusionRules#notInsideTag(HTML.Tag)}.
         */
        @SuppressWarnings("unused")
        public Builder notInsideTag(HTML.Tag tag) {
            return add(ExclusionRules.notInsideTag(tag));
        }

        /**
         * Shortcut for {@link ExclusionRules#notInsideAnyOf(HTML.Tag...)}.
         */
        public Builder notInsideAnyOf(HTML.Tag... tags) {
            return add(ExclusionRules.notInsideAnyOf(tags));
        }

        /**
         * Shortcut for {@link ExclusionRules#onlyWhenEditable()}.
         */
        @SuppressWarnings("unused")
        public Builder onlyWhenEditable() {
            return add(ExclusionRules.onlyWhenEditable());
        }

        /**
         * @return an immutable {@link ExclusionRuleSet} of the configured rules
         */
        public ExclusionRuleSet build() {
            return rules.isEmpty() ? EMPTY : new ExclusionRuleSet(rules);
        }
    }
}

