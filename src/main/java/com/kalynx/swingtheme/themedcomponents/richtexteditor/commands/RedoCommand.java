package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.undo.UndoManager;
import java.awt.event.KeyEvent;

public class RedoCommand extends AbstractEditorCommand {
    
    private final UndoManager undoManager;
    
    public RedoCommand(UndoManager undoManager) {
        super("Redo", null, CommandCategory.UNDO_REDO,
              KeyCombination.ctrl(KeyEvent.VK_Y),
              KeyCombination.ctrlShift(KeyEvent.VK_Z));
        this.undoManager = undoManager;
    }
    
    @Override
    public boolean isEnabled(EditorCommandContext context) {
        return undoManager != null && undoManager.canRedo();
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        if (undoManager != null && undoManager.canRedo()) {
            undoManager.redo();
        }
    }
}

