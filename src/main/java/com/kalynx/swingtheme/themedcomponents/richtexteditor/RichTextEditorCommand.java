package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.*;

public interface RichTextEditorCommand {

    String getTitle();

    Icon getIcon();

    KeyCombination[] getKeyCombinations();

    String getTooltip();

    boolean isEnabled(EditorCommandContext context);

    void execute(EditorCommandContext context);

    CommandCategory getCategory();

    /**
     * Returns an EnterKeyDelegate if this command wants to handle Enter key behavior
     * in its context (e.g., lists, headings). Returns null by default.
     *
     * @return EnterKeyDelegate or null if no custom Enter handling needed
     */
    default EnterKeyDelegate getEnterKeyDelegate() {
        return null;
    }

    enum CommandCategory {
        FORMATTING,
        EDITING,
        NAVIGATION,
        UNDO_REDO,
        INSERT,
        VIEW
    }
}


