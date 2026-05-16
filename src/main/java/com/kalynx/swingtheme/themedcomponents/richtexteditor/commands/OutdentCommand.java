package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.event.KeyEvent;

public class OutdentCommand extends AbstractEditorCommand {

    private static final int INDENT_PIXELS = 40;

    public OutdentCommand() {
        super("Outdent", null, CommandCategory.FORMATTING,
              KeyCombination.ctrlShift(KeyEvent.VK_TAB));
    }

    @Override
    public void execute(EditorCommandContext context) {
        Document doc = context.getDocument();
        int start = context.getCaretPosition();

        if (doc instanceof HTMLDocument) {
            HTMLDocument htmlDoc = (HTMLDocument) doc;
            Element paragraph = htmlDoc.getParagraphElement(start);

            if (isInListItem(paragraph)) {
                outdentListItem(htmlDoc, paragraph);
            } else {
                outdentParagraph(htmlDoc, paragraph);
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

    private void outdentListItem(HTMLDocument doc, Element paragraph) {
        try {
            Element listItem = findParentListItem(paragraph);
            if (listItem == null) {
                return;
            }

            MutableAttributeSet attrs = new SimpleAttributeSet(listItem.getAttributes());
            float currentIndent = StyleConstants.getLeftIndent(attrs);
            float newIndent = Math.max(0, currentIndent - INDENT_PIXELS);
            StyleConstants.setLeftIndent(attrs, newIndent);

            int start = listItem.getStartOffset();
            int length = listItem.getEndOffset() - start;
            doc.setParagraphAttributes(start, length, attrs, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void outdentParagraph(HTMLDocument doc, Element paragraph) {
        MutableAttributeSet attrs = new SimpleAttributeSet(paragraph.getAttributes());
        float currentIndent = StyleConstants.getLeftIndent(attrs);
        float newIndent = Math.max(0, currentIndent - INDENT_PIXELS);
        StyleConstants.setLeftIndent(attrs, newIndent);

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

