package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;

public class FontSizeCommand extends AbstractEditorCommand {
    
    private final int fontSize;
    
    public FontSizeCommand(int fontSize) {
        super("Font Size " + fontSize, null, CommandCategory.FORMATTING);
        this.fontSize = fontSize;
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
            StyleConstants.setFontSize(attrs, fontSize);
            htmlDoc.setCharacterAttributes(start, end - start, attrs, false);
        } else if (doc instanceof StyledDocument) {
            StyledDocument styledDoc = (StyledDocument) doc;
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontSize(attrs, fontSize);
            styledDoc.setCharacterAttributes(start, end - start, attrs, false);
        }
    }
}

