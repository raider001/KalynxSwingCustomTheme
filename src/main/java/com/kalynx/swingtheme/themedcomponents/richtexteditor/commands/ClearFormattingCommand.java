package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import java.awt.event.KeyEvent;

public class ClearFormattingCommand extends AbstractEditorCommand {

    public ClearFormattingCommand() {
        super("Clear Formatting", null, CommandCategory.FORMATTING,
              KeyCombination.ctrlShift(KeyEvent.VK_X));
    }

    @Override
    public void execute(EditorCommandContext context) {
        if (!context.hasSelection()) {
            return;
        }

        String plainText = context.getSelectedText();
        if (plainText != null) {
            context.replaceSelection(plainText);
        }
    }
}

