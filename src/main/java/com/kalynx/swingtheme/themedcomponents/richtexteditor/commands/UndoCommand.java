package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.undo.UndoManager;
import java.awt.event.KeyEvent;

public class UndoCommand extends AbstractEditorCommand {
    
    private final UndoManager undoManager;
    
    public UndoCommand(UndoManager undoManager) {
        super("Undo", null, CommandCategory.UNDO_REDO,
              KeyCombination.ctrl(KeyEvent.VK_Z));
        this.undoManager = undoManager;
    }
    
    @Override
    public boolean isEnabled(EditorCommandContext context) {
        return undoManager != null && undoManager.canUndo();
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        if (undoManager != null && undoManager.canUndo()) {
            undoManager.undo();
        }
    }
}

