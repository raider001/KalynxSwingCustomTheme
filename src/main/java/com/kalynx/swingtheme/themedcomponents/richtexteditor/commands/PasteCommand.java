package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;

public class PasteCommand extends AbstractEditorCommand {
    
    public PasteCommand() {
        super("Paste", null, CommandCategory.EDITING,
              KeyCombination.ctrl(KeyEvent.VK_V));
    }
    
    @Override
    public boolean isEnabled(EditorCommandContext context) {
        if (!context.isEditable()) {
            return false;
        }
        
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            return clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        context.getEditor().paste();
    }
}

