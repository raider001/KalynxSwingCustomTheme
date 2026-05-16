package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import java.awt.event.KeyEvent;

public class CopyCommand extends AbstractEditorCommand {
    
    public CopyCommand() {
        super("Copy", null, CommandCategory.EDITING,
              KeyCombination.ctrl(KeyEvent.VK_C));
    }
    
    @Override
    public boolean isEnabled(EditorCommandContext context) {
        return context.hasSelection();
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        context.getEditor().copy();
    }
}

