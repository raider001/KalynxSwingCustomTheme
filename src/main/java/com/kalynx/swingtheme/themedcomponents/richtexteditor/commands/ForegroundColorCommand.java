package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;

public class ForegroundColorCommand extends AbstractEditorCommand {
    
    private final Color color;
    
    public ForegroundColorCommand(String title, Color color) {
        super(title, null, CommandCategory.FORMATTING);
        this.color = color;
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        int start = context.getSelectionStart();
        int end = context.getSelectionEnd();
        
        if (start == end) {
            return;
        }
        
        Document doc = context.getDocument();
        if (doc instanceof HTMLDocument) {
            HTMLDocument htmlDoc = (HTMLDocument) doc;
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            htmlDoc.setCharacterAttributes(start, end - start, attrs, false);
        } else if (doc instanceof StyledDocument) {
            StyledDocument styledDoc = (StyledDocument) doc;
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            styledDoc.setCharacterAttributes(start, end - start, attrs, false);
        }
    }
}

