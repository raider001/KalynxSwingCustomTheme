package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import java.awt.event.KeyEvent;

public class SelectAllCommand extends AbstractEditorCommand {

    public SelectAllCommand() {
        super("Select All", null, CommandCategory.EDITING,
              KeyCombination.ctrl(KeyEvent.VK_A));
    }

    @Override
    public boolean isEnabled(EditorCommandContext context) {
        return true;
    }

    @Override
    public void execute(EditorCommandContext context) {
        context.getEditor().selectAll();
    }
}

