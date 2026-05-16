package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EnterKeyDelegate;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.ListEnterKeyDelegate;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.KeyEvent;

/**
 * Creates or removes unordered (bulleted) lists.
 *
 * HTML Structure Rule: Lists are always inserted at the root level of the document
 * (popDepth=1), never nested within paragraphs or headings. This ensures predictable
 * behavior with Java's HTMLEditorKit.
 */
public class UnorderedListCommand extends AbstractEditorCommand {

    private static final EnterKeyDelegate enterKeyDelegate = new ListEnterKeyDelegate();

    public UnorderedListCommand() {
        super("Unordered List", null, CommandCategory.FORMATTING,
              KeyCombination.ctrlShift(KeyEvent.VK_U));
    }

    @Override
    public EnterKeyDelegate getEnterKeyDelegate() {
        return enterKeyDelegate;
    }

    @Override
    public void execute(EditorCommandContext context) {
        Document doc = context.getDocument();
        if (!(doc instanceof HTMLDocument)) {
            return;
        }

        HTMLDocument htmlDoc = (HTMLDocument) doc;
        int start = context.getSelectionStart();
        int end = context.getSelectionEnd();

        try {
            Element paragraph = htmlDoc.getParagraphElement(start);
            Element parent = paragraph.getParentElement();

            if (isInList(parent)) {
                removeFromList(htmlDoc, paragraph);
            } else {
                insertUnorderedList(htmlDoc, start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isInList(Element element) {
        if (element == null) {
            return false;
        }
        HTML.Tag tag = (HTML.Tag) element.getAttributes().getAttribute(StyleConstants.NameAttribute);
        return HTML.Tag.OL.equals(tag) || HTML.Tag.UL.equals(tag);
    }

    private void removeFromList(HTMLDocument doc, Element paragraph) throws BadLocationException {
        int start = paragraph.getStartOffset();
        int end = paragraph.getEndOffset();
        String text = doc.getText(start, end - start);

        doc.remove(start, end - start);
        doc.insertString(start, text, new SimpleAttributeSet());
    }

    private void insertUnorderedList(HTMLDocument doc, int start, int end) throws Exception {
        int docLength = doc.getLength();

        if (docLength == 0) {
            HTMLEditorKit kit = new HTMLEditorKit();
            String html = "<ul><li></li></ul><p></p>";
            doc.insertString(0, " ", null);
            kit.insertHTML(doc, 0, html, 0, 0, null);
            doc.remove(doc.getLength() - 1, 1);
            return;
        }

        if (start == end) {
            Element para = doc.getParagraphElement(start);
            start = para.getStartOffset();
            end = para.getEndOffset();
        }

        start = Math.max(0, Math.min(start, docLength));
        end = Math.max(start, Math.min(end, docLength));

        if (start >= end) {
            HTMLEditorKit kit = new HTMLEditorKit();
            kit.insertHTML(doc, start, "<ul><li></li></ul><p></p>", 1, 0, null);
            return;
        }

        String selectedText = doc.getText(start, end - start).trim();

        if (selectedText.isEmpty()) {
            doc.remove(start, end - start);
            HTMLEditorKit kit = new HTMLEditorKit();
            kit.insertHTML(doc, start, "<ul><li></li></ul><p></p>", 1, 0, null);
            return;
        }

        String[] lines = selectedText.split("\n");

        StringBuilder listHtml = new StringBuilder("<ul>");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                listHtml.append("<li>").append(escapeHtml(trimmed)).append("</li>");
            }
        }

        if (listHtml.toString().equals("<ul>")) {
            listHtml.append("<li></li>");
        }
        listHtml.append("</ul><p></p>");

        doc.remove(start, end - start);

        HTMLEditorKit kit = new HTMLEditorKit();
        kit.insertHTML(doc, start, listHtml.toString(), 1, 0, null);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}







