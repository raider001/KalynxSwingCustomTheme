package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class CodeBlockCommand extends AbstractEditorCommand {

    public CodeBlockCommand() {
        super("Code Block", null, CommandCategory.FORMATTING, null);
    }

    @Override
    public void execute(EditorCommandContext context) {
        Document doc = context.getDocument();
        if (!(doc instanceof HTMLDocument)) {
            return;
        }

        HTMLDocument htmlDoc = (HTMLDocument) doc;
        int caretPos = context.getCaretPosition();

        try {
            Element paragraph = htmlDoc.getParagraphElement(caretPos);
            int start = paragraph.getStartOffset();
            int end = paragraph.getEndOffset();

            String content = htmlDoc.getText(start, end - start);
            content = content.replaceAll("[\n\r]+$", "");

            AttributeSet attrs = paragraph.getAttributes();
            HTML.Tag currentTag = (HTML.Tag) attrs.getAttribute(StyleConstants.NameAttribute);

            if (HTML.Tag.PRE.equals(currentTag)) {
                changeToNormal(htmlDoc, paragraph);
            } else {
                changeToCodeBlock(htmlDoc, paragraph);
            }

            context.setCaretPosition(Math.min(start + content.length(), htmlDoc.getLength()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeToCodeBlock(HTMLDocument doc, Element paragraph) throws Exception {
        int start = paragraph.getStartOffset();
        int end = paragraph.getEndOffset();

        start = Math.max(0, start);
        end = Math.min(end, doc.getLength());

        if (start >= end) {
            return;
        }

        String actualContent = doc.getText(start, end - start);
        actualContent = actualContent.replaceAll("[\n\r]+$", "");

        doc.remove(start, end - start);

        HTMLEditorKit kit = new HTMLEditorKit();
        String html = "<pre>" + escapeHtml(actualContent) + "</pre><p></p>";

        kit.insertHTML(doc, start, html, 1, 0, null);
    }

    private void changeToNormal(HTMLDocument doc, Element paragraph) throws Exception {
        int start = paragraph.getStartOffset();
        int end = paragraph.getEndOffset();

        String actualContent = doc.getText(start, end - start);
        actualContent = actualContent.replaceAll("[\n\r]+$", "");

        doc.remove(start, end - start);

        HTMLEditorKit kit = new HTMLEditorKit();
        String html = "<p>" + escapeHtml(actualContent) + "</p>";

        kit.insertHTML(doc, start, html, 1, 0, null);
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



