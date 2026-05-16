package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class FindCommand extends AbstractEditorCommand {
    
    public FindCommand() {
        super("Find", null, CommandCategory.NAVIGATION,
              KeyCombination.ctrl(KeyEvent.VK_F));
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        String searchText = JOptionPane.showInputDialog(
            context.getEditorPanel(),
            "Find:",
            "Find Text",
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (searchText != null && !searchText.isEmpty()) {
            String content = context.getText();
            int index = content.indexOf(searchText, context.getCaretPosition());
            
            if (index >= 0) {
                context.setCaretPosition(index);
                context.getEditor().select(index, index + searchText.length());
            } else {
                int fromStart = content.indexOf(searchText);
                if (fromStart >= 0) {
                    context.setCaretPosition(fromStart);
                    context.getEditor().select(fromStart, fromStart + searchText.length());
                } else {
                    JOptionPane.showMessageDialog(
                        context.getEditorPanel(),
                        "Text not found: " + searchText,
                        "Find",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        }
    }
}

