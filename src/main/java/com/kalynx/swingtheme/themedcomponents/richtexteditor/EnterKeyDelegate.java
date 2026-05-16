package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;

/**
 * Delegate interface for custom Enter key handling in specific contexts.
 *
 * Commands can implement this to provide custom behavior when Enter is pressed
 * while the cursor is in their context (e.g., within a list, heading, etc.).
 */
public interface EnterKeyDelegate {

    /**
     * Checks if this delegate can handle the Enter key in the current context.
     *
     * @param editor The editor pane
     * @param doc The HTML document
     * @param caretPos The current caret position
     * @return true if this delegate should handle the Enter key, false otherwise
     */
    boolean canHandle(JEditorPane editor, HTMLDocument doc, int caretPos);

    /**
     * Handles the Enter key press in the current context.
     *
     * @param editor The editor pane
     * @param doc The HTML document
     * @param caretPos The current caret position
     * @throws Exception if an error occurs during handling
     */
    void handleEnter(JEditorPane editor, HTMLDocument doc, int caretPos) throws Exception;
}

