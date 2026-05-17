package com.kalynx.swingtheme.themedcomponents.richtexteditor.exclusion;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;

import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import java.util.Arrays;

/**
 * Factory of common {@link ExclusionRule}s for rich-text editor commands.
 * <p>Rules returned by this factory are stateless and may be cached/shared
 * across commands.</p>
 */
public final class ExclusionRules {

    private ExclusionRules() {
    }

    /**
     * Blocks the command when the caret (or selection start) sits inside an
     * element whose tag matches the given HTML tag, anywhere in the ancestry.
     *
     * @param tag HTML tag the command is forbidden inside of
     * @return an {@link ExclusionRule}
     */
    public static ExclusionRule notInsideTag(HTML.Tag tag) {
        return notInsideAnyOf(tag);
    }

    /**
     * Blocks the command when the caret (or selection start) sits inside any
     * element whose tag matches one of the given HTML tags.
     *
     * @param tags HTML tags the command is forbidden inside of
     * @return an {@link ExclusionRule}
     */
    public static ExclusionRule notInsideAnyOf(HTML.Tag... tags) {
        HTML.Tag[] forbidden = tags.clone();
        return context -> {
            HTML.Tag ancestor = findForbiddenAncestor(context, forbidden);
            return ancestor != null ? "Command not allowed inside <" + ancestor + ">" : null;
        };
    }

    /**
     * Blocks the command when the editor is not editable (read-only).
     *
     * @return an {@link ExclusionRule}
     */
    public static ExclusionRule onlyWhenEditable() {
        return context -> context.isEditable() ? null : "Editor is read-only";
    }

    /**
     * @return {@code true} when the caret position is inside an element whose
     *         tag matches any tag in {@code tags}
     */
    @SuppressWarnings("unused")
    public static boolean isCaretInsideAnyOf(EditorCommandContext context, HTML.Tag... tags) {
        return findForbiddenAncestor(context, tags) != null;
    }

    private static HTML.Tag findForbiddenAncestor(EditorCommandContext context, HTML.Tag[] forbidden) {
        Document doc = context.getDocument();
        if (!(doc instanceof HTMLDocument htmlDoc) || forbidden.length == 0) {
            return null;
        }
        int pos = Math.min(context.getCaretPosition(), Math.max(0, doc.getLength()));
        Element element = htmlDoc.getCharacterElement(pos);
        while (element != null) {
            Object tagAttr = element.getAttributes().getAttribute(StyleConstants.NameAttribute);
            if (tagAttr instanceof HTML.Tag htmlTag && Arrays.asList(forbidden).contains(htmlTag)) {
                return htmlTag;
            }
            element = element.getParentElement();
        }
        return null;
    }
}

