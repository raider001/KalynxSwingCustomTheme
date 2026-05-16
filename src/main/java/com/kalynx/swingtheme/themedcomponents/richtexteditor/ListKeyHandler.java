package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.ActionEvent;

public class ListKeyHandler {

    public static void installKeyBindings(JEditorPane editorPane) {
        InputMap inputMap = editorPane.getInputMap();
        ActionMap actionMap = editorPane.getActionMap();

        KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");
        KeyStroke shiftEnterKey = KeyStroke.getKeyStroke("shift ENTER");

        inputMap.put(enterKey, "list-enter");
        inputMap.put(shiftEnterKey, "list-shift-enter");

        actionMap.put("list-enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEnter(editorPane);
            }
        });

        actionMap.put("list-shift-enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleShiftEnter(editorPane);
            }
        });
    }

    private static void handleEnter(JEditorPane editorPane) {
        Document doc = editorPane.getDocument();
        if (!(doc instanceof HTMLDocument)) {
            performDefaultEnter(editorPane);
            return;
        }

        HTMLDocument htmlDoc = (HTMLDocument) doc;
        int caretPos = editorPane.getCaretPosition();

        try {
            Element paragraph = htmlDoc.getParagraphElement(caretPos);

            if (isListItem(paragraph)) {
                int start = paragraph.getStartOffset();
                int end = paragraph.getEndOffset();
                String content = htmlDoc.getText(start, end - start).trim();

                if (content.isEmpty()) {
                    exitList(editorPane, htmlDoc, paragraph);
                } else {
                    createNewListItem(editorPane, htmlDoc, paragraph, caretPos);
                }
            } else {
                performDefaultEnter(editorPane);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            performDefaultEnter(editorPane);
        }
    }

    private static void handleShiftEnter(JEditorPane editorPane) {
        System.out.println("DEBUG: handleShiftEnter called");
        Document doc = editorPane.getDocument();
        if (!(doc instanceof HTMLDocument)) {
            performDefaultShiftEnter(editorPane);
            return;
        }

        HTMLDocument htmlDoc = (HTMLDocument) doc;
        int caretPos = editorPane.getCaretPosition();

        try {
            Element paragraph = htmlDoc.getParagraphElement(caretPos);
            System.out.println("DEBUG: Is list item for Shift+Enter: " + isListItem(paragraph));

            if (isListItem(paragraph)) {
                System.out.println("DEBUG: Adding line break in list item");
                htmlDoc.insertString(caretPos, "\n", null);
            } else {
                performDefaultShiftEnter(editorPane);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            performDefaultShiftEnter(editorPane);
        }
    }

    private static boolean isListItem(Element element) {
        if (element == null) {
            return false;
        }
        AttributeSet attrs = element.getAttributes();
        Object nameAttr = attrs.getAttribute(StyleConstants.NameAttribute);
        HTML.Tag tag = (HTML.Tag) nameAttr;
        return HTML.Tag.LI.equals(tag);
    }

    private static boolean isInList(Element element) {
        if (element == null) {
            return false;
        }
        HTML.Tag tag = (HTML.Tag) element.getAttributes().getAttribute(StyleConstants.NameAttribute);
        return HTML.Tag.UL.equals(tag) || HTML.Tag.OL.equals(tag);
    }

    private static void exitList(JEditorPane editorPane, HTMLDocument doc, Element listItem) {
        try {
            Element list = listItem.getParentElement();
            if (!isInList(list)) {
                performDefaultEnter(editorPane);
                return;
            }

            int itemStart = listItem.getStartOffset();
            int itemEnd = listItem.getEndOffset();
            int listEnd = list.getEndOffset();

            doc.remove(itemStart, itemEnd - itemStart);

            int insertPos = Math.min(listEnd - (itemEnd - itemStart), doc.getLength());

            HTMLEditorKit kit = (HTMLEditorKit) editorPane.getEditorKit();
            doc.insertString(insertPos, "\n", null);

            SwingUtilities.invokeLater(() -> {
                try {
                    editorPane.setCaretPosition(Math.min(insertPos + 1, doc.getLength()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            performDefaultEnter(editorPane);
        }
    }

    private static void createNewListItem(JEditorPane editorPane, HTMLDocument doc,
                                          Element listItem, int caretPos) {
        try {
            Element list = listItem.getParentElement();
            if (!isInList(list)) {
                performDefaultEnter(editorPane);
                return;
            }

            int itemStart = listItem.getStartOffset();
            int itemEnd = listItem.getEndOffset();
            int offsetInItem = caretPos - itemStart;

            String fullContent = doc.getText(itemStart, itemEnd - itemStart);
            String beforeCaret = fullContent.substring(0, Math.min(offsetInItem, fullContent.length()));
            String afterCaret = fullContent.substring(Math.min(offsetInItem, fullContent.length()));

            beforeCaret = beforeCaret.replaceAll("[\n\r]+$", "");
            afterCaret = afterCaret.replaceAll("^[\n\r]+", "");

            doc.remove(itemStart, itemEnd - itemStart);

            HTML.Tag listTag = (HTML.Tag) list.getAttributes().getAttribute(StyleConstants.NameAttribute);
            String tagName = HTML.Tag.UL.equals(listTag) ? "ul" : "ol";

            StringBuilder html = new StringBuilder();
            html.append("<li>").append(escapeHtml(beforeCaret)).append("</li>");
            html.append("<li>").append(escapeHtml(afterCaret)).append("</li>");

            HTMLEditorKit kit = (HTMLEditorKit) editorPane.getEditorKit();
            kit.insertHTML(doc, itemStart, html.toString(), 0, 0, listTag);

            int newCaretPos = itemStart + beforeCaret.length() + 4;

            SwingUtilities.invokeLater(() -> {
                try {
                    editorPane.setCaretPosition(Math.min(newCaretPos, doc.getLength()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            performDefaultEnter(editorPane);
        }
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    private static void performDefaultEnter(JEditorPane editorPane) {
        try {
            Document doc = editorPane.getDocument();
            int caretPos = editorPane.getCaretPosition();
            doc.insertString(caretPos, "\n", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void performDefaultShiftEnter(JEditorPane editorPane) {
        try {
            Document doc = editorPane.getDocument();
            int caretPos = editorPane.getCaretPosition();
            doc.insertString(caretPos, "\n", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





