package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractEditorCommand implements RichTextEditorCommand {

    private final String title;
    private final Icon icon;
    private final KeyCombination[] keyCombinations;
    private final CommandCategory category;

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
        return context.isEditable();
    }
}

