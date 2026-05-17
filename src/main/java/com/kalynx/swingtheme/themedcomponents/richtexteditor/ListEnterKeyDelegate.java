package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Handles Enter key behavior when the cursor is within a list.
 * <p>Behavior:</p>
 * <ul>
 *   <li>Enter on non-empty list item: Creates a new list item below</li>
 *   <li>Enter on empty list item: Exits the list</li>
 *   <li>Shift+Enter: Handled separately (line break within item)</li>
 * </ul>
 */
public class ListEnterKeyDelegate implements EnterKeyDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListEnterKeyDelegate.class);

    private enum Operation {
        EXIT_LIST,
        END_OF_LINE_CREATE_NEW_ITEM,
        LINE_BREAK_IN_ITEM
    }

    private JEditorPane currentEditor;
    private HTMLDocument currentDoc;
    private Element currentListItem;
    private int currentCaretPos;

    @Override
    public boolean canHandle(JEditorPane editor, HTMLDocument doc, int caretPos) {
        try {
            Element paragraph = doc.getParagraphElement(caretPos);
            if (isListItem(paragraph)) {
                return true;
            }

            Element charElement = doc.getCharacterElement(caretPos);
            Element parent = charElement.getParentElement();
            int depth = 0;
            while (parent != null && depth < 5) {
                if (isListItem(parent)) {
                    return true;
                }
                parent = parent.getParentElement();
                depth++;
            }
        } catch (Exception e) {
            LOGGER.warn("Error checking canHandle", e);
        }
        return false;
    }

    @Override
    public void handleEnter(JEditorPane editor, HTMLDocument doc, int caretPos) throws Exception {
        this.currentEditor = editor;
        this.currentDoc = doc;
        this.currentCaretPos = caretPos;
        this.currentListItem = findListItem(doc, caretPos);

        if (currentListItem == null) {
            return;
        }

        Operation operation = determineOperation();

        switch(operation) {
            case EXIT_LIST -> performExitListOperation();
            case END_OF_LINE_CREATE_NEW_ITEM -> performEndOfLineCreateNewItemOperation();
            case LINE_BREAK_IN_ITEM -> performLineBreakInItemOperation();
        }
    }

    private Operation determineOperation() throws Exception {
        int itemStart = currentListItem.getStartOffset();
        int itemEnd = currentListItem.getEndOffset();
        String content = currentDoc.getText(itemStart, itemEnd - itemStart).trim();

        if (content.isEmpty()) {
            return Operation.EXIT_LIST;
        }

        int offsetInItem = currentCaretPos - itemStart;
        String fullContent = currentDoc.getText(itemStart, itemEnd - itemStart);
        String afterCaret = fullContent.substring(Math.min(offsetInItem, fullContent.length()));
        afterCaret = afterCaret.replaceAll("^[\n\r]+", "");

        if (afterCaret.isEmpty()) {
            return Operation.END_OF_LINE_CREATE_NEW_ITEM;
        }

        return Operation.LINE_BREAK_IN_ITEM;
    }

    private void performLineBreakInItemOperation() throws Exception {
        int itemStart = currentListItem.getStartOffset();
        int itemEnd = currentListItem.getEndOffset();
        int offsetInItem = currentCaretPos - itemStart;

        String fullContent = currentDoc.getText(itemStart, itemEnd - itemStart);

        String afterCaret = fullContent.substring(Math.min(offsetInItem, fullContent.length()));

        if (afterCaret.isEmpty()) {
            return;
        }

        int removeStart = currentCaretPos;
        int removeEnd = itemEnd - 1;
        int charsToRemove = removeEnd - removeStart;

        if (charsToRemove > 0) {
            currentDoc.remove(removeStart, charsToRemove);
        }

        Element updatedItem = findListItem(currentDoc, itemStart);
        if (updatedItem != null) {
            String newItemHtml = "<li>" + escapeHtml(afterCaret) + "</li>";
            currentDoc.insertAfterEnd(updatedItem, newItemHtml);

            final int newListItemStart = updatedItem.getEndOffset();
            SwingUtilities.invokeLater(() -> {
                try {
                    currentEditor.setCaretPosition(Math.min(newListItemStart, currentDoc.getLength()));
                } catch (Exception e) {
                    LOGGER.debug("Could not restore caret after line break", e);
                }
            });
        }
    }

    private void performEndOfLineCreateNewItemOperation() throws Exception {
        String newItemHtml = "<li></li>";
        currentDoc.insertAfterEnd(currentListItem, newItemHtml);

        final int newListItemStart = currentListItem.getEndOffset();
        SwingUtilities.invokeLater(() -> {
            try {
                currentEditor.setCaretPosition(Math.min(newListItemStart, currentDoc.getLength()));
            } catch (Exception e) {
                LOGGER.debug("Could not restore caret after new item", e);
            }
        });
    }

    private void performExitListOperation() throws Exception {
        Element list = currentListItem.getParentElement();
        if (!isInList(list)) {
            return;
        }

        int itemStart = currentListItem.getStartOffset();

        HTMLEditorKit kit = (HTMLEditorKit) currentEditor.getEditorKit();
        
            try {
                kit.insertHTML(currentDoc, list.getEndOffset(), "<p> </p>", 1, 0, HTML.Tag.P);
                Thread.sleep(100);
            } catch (Exception e) {
                try {
                    currentDoc.insertAfterEnd(list, "<p> </p>");
                    Thread.sleep(100);
                } catch (Exception e2) {
                    LOGGER.debug("Could not insert paragraph after list", e2);
                }
            }

        Element updatedList = findListElementAt(currentDoc, Math.max(0, itemStart - 1));
        if (updatedList != null && updatedList.getElementCount() > 0) {
            Element lastItem = updatedList.getElement(updatedList.getElementCount() - 1);
            
            if (isListItem(lastItem)) {
                int lastItemStart = lastItem.getStartOffset();
                int lastItemEnd = lastItem.getEndOffset();
                String lastItemText = currentDoc.getText(lastItemStart, lastItemEnd - lastItemStart).trim();
                
                if (lastItemText.isEmpty()) {
                    currentDoc.remove(lastItemStart, lastItemEnd - lastItemStart);
                }
            }
        }

        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(50);

                Element updatedListAgain = findListElementAt(currentDoc, Math.max(0, itemStart - 1));
                int searchStart = updatedListAgain != null ? updatedListAgain.getEndOffset() : itemStart;

                for (int i = searchStart; i < currentDoc.getLength(); i++) {
                    Element elem = currentDoc.getParagraphElement(i);
                    HTML.Tag tag = (HTML.Tag) elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
                    if (HTML.Tag.P.equals(tag)) {
                        currentEditor.setCaretPosition(elem.getStartOffset());
                        return;
                    }
                }
                currentEditor.setCaretPosition(Math.min(itemStart, currentDoc.getLength()));
            } catch (Exception e) {
                LOGGER.debug("Could not restore caret after exit list", e);
            }
        });
    }

    private Element findListItem(HTMLDocument doc, int caretPos) {
        Element paragraph = doc.getParagraphElement(caretPos);
        if (isListItem(paragraph)) {
            return paragraph;
        }

        Element charElement = doc.getCharacterElement(caretPos);
        Element parent = charElement.getParentElement();
        int depth = 0;
        while (parent != null && depth < 5) {
            if (isListItem(parent)) {
                return parent;
            }
            parent = parent.getParentElement();
            depth++;
        }

        return null;
    }

    private Element findListElementAt(HTMLDocument doc, int caretPos) {
        Element charElement = doc.getCharacterElement(caretPos);
        Element parent = charElement.getParentElement();
        int depth = 0;
        while (parent != null && depth < 10) {
            if (isInList(parent)) {
                return parent;
            }
            parent = parent.getParentElement();
            depth++;
        }

        return null;
    }

    private boolean isListItem(Element element) {
        if (element == null) {
            return false;
        }
        HTML.Tag tag = (HTML.Tag) element.getAttributes().getAttribute(StyleConstants.NameAttribute);
        return HTML.Tag.LI.equals(tag);
    }

    private boolean isInList(Element element) {
        if (element == null) {
            return false;
        }
        HTML.Tag tag = (HTML.Tag) element.getAttributes().getAttribute(StyleConstants.NameAttribute);
        return HTML.Tag.UL.equals(tag) || HTML.Tag.OL.equals(tag);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";

        String escaped = text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");

        if (escaped.startsWith(" ")) {
            escaped = "&nbsp;" + escaped.substring(1);
        }

        return escaped;
    }

}




