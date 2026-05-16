package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.event.KeyEvent;

public class ItalicCommand extends AbstractEditorCommand {
    
    public ItalicCommand() {
        super("Italic", null, CommandCategory.FORMATTING,
              KeyCombination.ctrl(KeyEvent.VK_I));
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
            
            Element element = htmlDoc.getCharacterElement(start);
            AttributeSet existingAttrs = element.getAttributes();
            boolean isItalic = StyleConstants.isItalic(existingAttrs);
            
            StyleConstants.setItalic(attrs, !isItalic);
            htmlDoc.setCharacterAttributes(start, end - start, attrs, false);
        } else if (doc instanceof StyledDocument) {
            StyledDocument styledDoc = (StyledDocument) doc;
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            
            Element element = styledDoc.getCharacterElement(start);
            AttributeSet existingAttrs = element.getAttributes();
            boolean isItalic = StyleConstants.isItalic(existingAttrs);
            
            StyleConstants.setItalic(attrs, !isItalic);
            styledDoc.setCharacterAttributes(start, end - start, attrs, false);
        }
    }
}

