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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Changes the block-level format of the caret's paragraph (or every paragraph
 * in the current selection) to Normal, Paragraph, or Heading (H1-H5).
 *
 * <p>Approved block tags are {@code <p>} and {@code <h1>}-{@code <h5>}. When the
 * current block tag is one of the approved tags, it is swapped for the target
 * tag via {@link HTMLDocument#setOuterHTML(Element, String)}. When the current
 * block is something else (e.g. {@code <li>} inside a list), the new tag is
 * wrapped <em>inside</em> the existing block via
 * {@link HTMLDocument#setInnerHTML(Element, String)} so the surrounding
 * structure (lists, tables, etc.) is preserved.</p>
 *
 * <p>Inline formatting (bold / italic / underline) carried by leaf elements is
 * preserved across the format change.</p>
 */
public class FormatCommand extends AbstractEditorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormatCommand.class);

    private static final Set<HTML.Tag> APPROVED_BLOCK_TAGS = Set.of(
        HTML.Tag.P, HTML.Tag.H1, HTML.Tag.H2, HTML.Tag.H3, HTML.Tag.H4, HTML.Tag.H5
    );

    private final String format;

    private enum Mode { SELECTION, LINE }

    /**
     * @param format one of {@code Normal}, {@code Paragraph}, {@code H1}-{@code H5}
     */
    public FormatCommand(String format) {
        super("Format: " + format, null, CommandCategory.FORMATTING, new KeyCombination[0]);
        this.format = format;
    }

    @Override
    protected ExclusionRuleSet buildExclusionRules() {
        return ExclusionRuleSet.builder()
            .notInsideAnyOf(HTML.Tag.PRE, HTML.Tag.CODE)
            .build();
    }

    @Override
    public void execute(EditorCommandContext context) {
        if (!(context.getDocument() instanceof HTMLDocument htmlDoc)) {
            return;
        }
        HTML.Tag targetTag = resolveTargetTag();
        if (targetTag == null) {
            return;
        }

        Mode mode = context.hasSelection() ? Mode.SELECTION : Mode.LINE;
        try {
            switch (mode) {
                case LINE -> handleLineOperation(context, htmlDoc, targetTag);
                case SELECTION -> handleSelectionOperation(context, htmlDoc, targetTag);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to apply format '{}'", format, e);
        }
    }

    /**
     * Reformats only the paragraph that contains the caret.
     */
    private void handleLineOperation(EditorCommandContext context, HTMLDocument doc, HTML.Tag targetTag)
        throws BadLocationException, java.io.IOException {
        Element paragraph = doc.getParagraphElement(context.getCaretPosition());
        if (paragraph == null) {
            return;
        }

        int caretOffsetInParagraph = context.getCaretPosition() - paragraph.getStartOffset();
        int paragraphStart = paragraph.getStartOffset();

        applyFormat(doc, paragraph, targetTag);

        restoreCaret(context, doc, paragraphStart, caretOffsetInParagraph);
    }

    /**
     * Reformats every paragraph that overlaps the current selection.
     */
    private void handleSelectionOperation(EditorCommandContext context, HTMLDocument doc, HTML.Tag targetTag)
        throws BadLocationException, java.io.IOException {
        int selectionStart = context.getSelectionStart();
        int selectionEnd = context.getSelectionEnd();

        List<Integer> paragraphStarts = collectParagraphStarts(doc, selectionStart, selectionEnd);
        if (paragraphStarts.isEmpty()) {
            return;
        }

        for (int i = paragraphStarts.size() - 1; i >= 0; i--) {
            int startOffset = paragraphStarts.get(i);
            if (startOffset >= doc.getLength()) {
                continue;
            }
            Element paragraph = doc.getParagraphElement(startOffset);
            if (paragraph != null) {
                applyFormat(doc, paragraph, targetTag);
            }
        }

        int newCaret = Math.min(selectionEnd, doc.getLength());
        context.setCaretPosition(newCaret);
    }

    private List<Integer> collectParagraphStarts(HTMLDocument doc, int from, int to) {
        List<Integer> starts = new ArrayList<>();
        int pos = from;
        int limit = Math.min(to, doc.getLength());
        while (pos <= limit) {
            Element paragraph = doc.getParagraphElement(pos);
            if (paragraph == null) {
                break;
            }
            int paragraphStart = paragraph.getStartOffset();
            int paragraphEnd = paragraph.getEndOffset();
            if (starts.isEmpty() || starts.getLast() != paragraphStart) {
                starts.add(paragraphStart);
            }
            if (paragraphEnd <= pos) {
                break;
            }
            pos = paragraphEnd;
        }
        return starts;
    }

    private void applyFormat(HTMLDocument doc, Element paragraph, HTML.Tag targetTag)
        throws BadLocationException, java.io.IOException {
        HTML.Tag currentTag = blockTagOf(paragraph);
        if (targetTag.equals(currentTag)) {
            return;
        }

        String innerHtml = serializeInner(doc, paragraph);
        String tagName = targetTag.toString();
        String replacement = "<" + tagName + ">" + innerHtml + "</" + tagName + ">";

        if (APPROVED_BLOCK_TAGS.contains(currentTag)) {
            doc.setOuterHTML(paragraph, replacement);
        } else {
            doc.setInnerHTML(paragraph, replacement);
        }
    }

    private void restoreCaret(EditorCommandContext context, HTMLDocument doc, int originalParagraphStart,
                              int caretOffsetInParagraph) {
        int target = originalParagraphStart;
        Element newParagraph = doc.getParagraphElement(Math.min(originalParagraphStart, doc.getLength()));
        if (newParagraph != null) {
            int paragraphLength = newParagraph.getEndOffset() - newParagraph.getStartOffset();
            int safeOffset = Math.max(0, Math.min(caretOffsetInParagraph, Math.max(0, paragraphLength - 1)));
            target = newParagraph.getStartOffset() + safeOffset;
        }
        context.setCaretPosition(Math.min(target, doc.getLength()));
    }

    private HTML.Tag resolveTargetTag() {
        if ("Normal".equals(format) || "Paragraph".equals(format)) {
            return HTML.Tag.P;
        }
        return getHeadingTag(format);
    }

    private HTML.Tag getHeadingTag(String format) {
        return switch (format) {
            case "H1" -> HTML.Tag.H1;
            case "H2" -> HTML.Tag.H2;
            case "H3" -> HTML.Tag.H3;
            case "H4" -> HTML.Tag.H4;
            case "H5" -> HTML.Tag.H5;
            default -> null;
        };
    }

    private HTML.Tag blockTagOf(Element element) {
        Object tag = element.getAttributes().getAttribute(StyleConstants.NameAttribute);
        return tag instanceof HTML.Tag htmlTag ? htmlTag : null;
    }

    private String serializeInner(HTMLDocument doc, Element parent) throws BadLocationException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parent.getElementCount(); i++) {
            serializeNode(doc, parent.getElement(i), sb);
        }
        return sb.toString();
    }

    private void serializeNode(HTMLDocument doc, Element element, StringBuilder sb) throws BadLocationException {
        if (!element.isLeaf()) {
            for (int i = 0; i < element.getElementCount(); i++) {
                serializeNode(doc, element.getElement(i), sb);
            }
            return;
        }

        int start = element.getStartOffset();
        int end = element.getEndOffset();
        String text = doc.getText(start, end - start);
        text = text.replaceAll("[\n\r]+$", "");
        if (text.isEmpty()) {
            return;
        }

        AttributeSet attrs = element.getAttributes();
        boolean bold = StyleConstants.isBold(attrs);
        boolean italic = StyleConstants.isItalic(attrs);
        boolean underline = StyleConstants.isUnderline(attrs);

        StringBuilder open = new StringBuilder();
        StringBuilder close = new StringBuilder();
        if (bold) {
            open.append("<b>");
            close.insert(0, "</b>");
        }
        if (italic) {
            open.append("<i>");
            close.insert(0, "</i>");
        }
        if (underline) {
            open.append("<u>");
            close.insert(0, "</u>");
        }

        sb.append(open).append(escapeHtml(text)).append(close);
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    /**
     * @return the configured format identifier
     */
    public String getFormat() {
        return format;
    }
}
