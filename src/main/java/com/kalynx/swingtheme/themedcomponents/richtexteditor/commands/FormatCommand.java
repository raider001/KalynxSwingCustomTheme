package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Changes the format of the current paragraph to Normal, Paragraph, or Heading (H1-H5).
 *
 * HTML Structure Rule: All block elements (paragraphs, headings) are maintained at the
 * root level of the document (popDepth=1). When changing formats, the current block
 * element is replaced with a new one of the target type.
 */
public class FormatCommand extends AbstractEditorCommand {

    private final String format;

    public FormatCommand(String format) {
        super("Format: " + format, null, CommandCategory.FORMATTING, null);
        this.format = format;
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

            if (format.equals("Normal")) {
                if (currentTag != null && !currentTag.equals(HTML.Tag.IMPLIED)) {
                    htmlDoc.remove(start, end - start);
                    htmlDoc.insertString(start, content + "\n", new SimpleAttributeSet());
                }
            } else if (format.equals("Paragraph")) {
                changeFormat(htmlDoc, paragraph, content, HTML.Tag.P);
            } else if (format.startsWith("H")) {
                HTML.Tag newTag = getHeadingTag(format);
                if (newTag != null) {
                    changeFormat(htmlDoc, paragraph, content, newTag);
                }
            }

            context.setCaretPosition(Math.min(start + content.length(), htmlDoc.getLength()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeFormat(HTMLDocument doc, Element paragraph, String content, HTML.Tag newTag) throws Exception {
        AttributeSet currentAttrs = paragraph.getAttributes();
        HTML.Tag currentTag = (HTML.Tag) currentAttrs.getAttribute(StyleConstants.NameAttribute);

        if (newTag.equals(currentTag)) {
            return;
        }

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
        String tagName = newTag.toString();
        String html = "<" + tagName + ">" + escapeHtml(actualContent) + "</" + tagName + "><p></p>";

        kit.insertHTML(doc, start, html, 1, 0, null);
    }

    private HTML.Tag getHeadingTag(String format) {
        switch (format) {
            case "H1": return HTML.Tag.H1;
            case "H2": return HTML.Tag.H2;
            case "H3": return HTML.Tag.H3;
            case "H4": return HTML.Tag.H4;
            case "H5": return HTML.Tag.H5;
            default: return null;
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    public String getFormat() {
        return format;
    }
}




