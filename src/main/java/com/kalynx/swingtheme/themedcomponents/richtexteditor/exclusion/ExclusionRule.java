package com.kalynx.swingtheme.themedcomponents.richtexteditor.exclusion;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;

/**
 * Predicate that decides whether a command may run in the current editor
 * context. A non-null, non-blank return value means the rule blocks execution
 * and supplies a human-readable reason (suitable for logging or tooltips).
 */
@FunctionalInterface
public interface ExclusionRule {

    /**
     * Evaluates this rule against the given editor context.
     *
     * @param context the editor context to inspect
     * @return {@code null} (or an empty string) when the command is allowed;
     *         otherwise a reason describing why the command is blocked
     */
    String evaluate(EditorCommandContext context);
}

