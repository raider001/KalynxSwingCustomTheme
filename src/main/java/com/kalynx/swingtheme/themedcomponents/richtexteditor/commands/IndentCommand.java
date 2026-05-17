package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.exclusion.ExclusionRuleSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.event.KeyEvent;

public class IndentCommand extends AbstractEditorCommand {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IndentCommand.class);
    private static final int INDENT_PIXELS = 40;
    
    public IndentCommand() {
        super("Indent", null, CommandCategory.FORMATTING, 
              KeyCombination.simple(KeyEvent.VK_TAB));
    }
    
    @Override
    protected ExclusionRuleSet buildExclusionRules() {
        return ExclusionRuleSet.builder()
            .notInsideAnyOf(HTML.Tag.PRE, HTML.Tag.CODE)
            .build();
    }

    @Override
    public void execute(EditorCommandContext context) {
        Document doc = context.getDocument();
        int start = context.getCaretPosition();
        
        if (doc instanceof HTMLDocument htmlDoc) {
            Element paragraph = htmlDoc.getParagraphElement(start);

            if (isInListItem(paragraph)) {
                indentListItem(htmlDoc, paragraph);
            } else {
                indentParagraph(htmlDoc, paragraph);
            }
        }
    }
    
    @Override
    public boolean isEnabled(EditorCommandContext context) {
        return super.isEnabled(context) && !context.hasSelection();
    }
    
    private boolean isInListItem(Element element) {
        while (element != null) {
            HTML.Tag tag = (HTML.Tag) element.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (HTML.Tag.LI.equals(tag)) {
                return true;
            }
            element = element.getParentElement();
        }
        return false;
    }
    
    private void indentListItem(HTMLDocument doc, Element paragraph) {
        try {
            Element listItem = findParentListItem(paragraph);
            if (listItem == null) {
                return;
            }
            
            MutableAttributeSet attrs = new SimpleAttributeSet(listItem.getAttributes());
            float currentIndent = StyleConstants.getLeftIndent(attrs);
            StyleConstants.setLeftIndent(attrs, currentIndent + INDENT_PIXELS);
            
            int start = listItem.getStartOffset();
            int length = listItem.getEndOffset() - start;
            doc.setParagraphAttributes(start, length, attrs, false);
        } catch (Exception e) {
            LOGGER.warn("Failed to indent list item", e);
        }
    }
    
    private void indentParagraph(HTMLDocument doc, Element paragraph) {
        MutableAttributeSet attrs = new SimpleAttributeSet(paragraph.getAttributes());
        float currentIndent = StyleConstants.getLeftIndent(attrs);
        StyleConstants.setLeftIndent(attrs, currentIndent + INDENT_PIXELS);
        
        int start = paragraph.getStartOffset();
        int length = paragraph.getEndOffset() - start;
        doc.setParagraphAttributes(start, length, attrs, false);
    }
    
    private Element findParentListItem(Element element) {
        while (element != null) {
            HTML.Tag tag = (HTML.Tag) element.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (HTML.Tag.LI.equals(tag)) {
                return element;
            }
            element = element.getParentElement();
        }
        return null;
    }
}
