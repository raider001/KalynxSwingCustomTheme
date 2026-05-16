package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import java.awt.event.KeyEvent;

public class CutCommand extends AbstractEditorCommand {
    
    public CutCommand() {
        super("Cut", null, CommandCategory.EDITING,
              KeyCombination.ctrl(KeyEvent.VK_X));
    }
    
    @Override
    public boolean isEnabled(EditorCommandContext context) {
        return context.isEditable() && context.hasSelection();
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        context.getEditor().cut();
    }
}

