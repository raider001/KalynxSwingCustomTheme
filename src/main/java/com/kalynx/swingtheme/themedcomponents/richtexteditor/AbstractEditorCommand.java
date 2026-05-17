package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.exclusion.ExclusionRuleSet;

import javax.swing.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Base class for editor commands. Subclasses may override
 * {@link #buildExclusionRules()} to declare scenarios in which the command
 * must not run; the resulting {@link ExclusionRuleSet} is consulted by
 * {@link #isEnabled(EditorCommandContext)} so every dispatch path
 * (toolbar buttons, key bindings, {@code EditorCommandManager#executeCommand})
 * is gated consistently.
 */
public abstract class AbstractEditorCommand implements RichTextEditorCommand {

    private final String title;
    private final Icon icon;
    private final KeyCombination[] keyCombinations;
    private final CommandCategory category;
    private volatile ExclusionRuleSet exclusionRules;

    public AbstractEditorCommand(String title, Icon icon, CommandCategory category, KeyCombination... keyCombinations) {
        this.title = title;
        this.icon = icon;
        this.category = category;
        this.keyCombinations = keyCombinations != null ? keyCombinations : new KeyCombination[0];
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public KeyCombination[] getKeyCombinations() {
        return keyCombinations;
    }

    @Override
    public String getTooltip() {
        if (keyCombinations.length == 0) {
            return title;
        }

        String shortcuts = Arrays.stream(keyCombinations)
                .map(KeyCombination::toDisplayString)
                .collect(Collectors.joining(", "));

        return title + " (" + shortcuts + ")";
    }

    @Override
    public CommandCategory getCategory() {
        return category;
    }

    @Override
    public boolean isEnabled(EditorCommandContext context) {
        return context.isEditable() && getExclusionRules().isAllowed(context);
    }

    /**
     * @return the resolved exclusion rule set for this command (lazy, cached)
     */
    public final ExclusionRuleSet getExclusionRules() {
        ExclusionRuleSet cached = exclusionRules;
        if (cached == null) {
            cached = buildExclusionRules();
            if (cached == null) {
                cached = ExclusionRuleSet.empty();
            }
            exclusionRules = cached;
        }
        return cached;
    }

    /**
     * Subclasses override to declare contexts in which this command must not
     * run. Default implementation returns {@link ExclusionRuleSet#empty()}.
     *
     * @return an {@link ExclusionRuleSet} describing forbidden contexts
     */
    protected ExclusionRuleSet buildExclusionRules() {
        return ExclusionRuleSet.empty();
    }
}
