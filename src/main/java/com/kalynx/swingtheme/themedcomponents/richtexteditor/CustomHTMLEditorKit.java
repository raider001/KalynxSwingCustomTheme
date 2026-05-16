package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class CustomHTMLEditorKit extends HTMLEditorKit {

    private static Action defaultInsertBreakAction = null;
    private static final List<EnterKeyDelegate> enterKeyDelegates = new ArrayList<>();

    public static void registerEnterKeyDelegate(EnterKeyDelegate delegate) {
        if (delegate != null && !enterKeyDelegates.contains(delegate)) {
            enterKeyDelegates.add(delegate);
        }
    }

    public static void clearEnterKeyDelegates() {
        enterKeyDelegates.clear();
    }

    @Override
    public ViewFactory getViewFactory() {
        return super.getViewFactory();
    }

    @Override
    protected void createInputAttributes(Element element, MutableAttributeSet set) {
        super.createInputAttributes(element, set);
    }

    public static class InsertBreakAction extends TextAction {

        public InsertBreakAction() {
            super("insert-break");
            if (defaultInsertBreakAction == null) {
                HTMLEditorKit defaultKit = new HTMLEditorKit();
                Action[] defaultActions = defaultKit.getActions();
                for (Action action : defaultActions) {
                    if ("insert-break".equals(action.getValue(Action.NAME))) {
                        defaultInsertBreakAction = action;
                        break;
                    }
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor == null) {
                return;
            }

            Document doc = editor.getDocument();
            if (!(doc instanceof HTMLDocument)) {
                if (defaultInsertBreakAction != null) {
                    defaultInsertBreakAction.actionPerformed(e);
                }
                return;
            }

            HTMLDocument htmlDoc = (HTMLDocument) doc;
            int caretPos = editor.getCaretPosition();

            try {
                // Check if any registered delegate can handle this
                for (EnterKeyDelegate delegate : enterKeyDelegates) {
                    if (delegate.canHandle(editor, htmlDoc, caretPos)) {
                        delegate.handleEnter(editor, htmlDoc, caretPos);
                        return;
                    }
                }

                // No delegate handled it, use default behavior
                if (defaultInsertBreakAction != null) {
                    defaultInsertBreakAction.actionPerformed(e);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                if (defaultInsertBreakAction != null) {
                    defaultInsertBreakAction.actionPerformed(e);
                }
            }
        }

        private JEditorPane getEditor(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target instanceof JEditorPane) {
                return (JEditorPane) target;
            }
            return null;
        }
    }

    public static class InsertLineBreakAction extends TextAction {

        public InsertLineBreakAction() {
            super("insert-line-break");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target == null) {
                return;
            }

            Document doc = target.getDocument();
            if (!(doc instanceof HTMLDocument)) {
                insertLineBreak(target);
                return;
            }

            HTMLDocument htmlDoc = (HTMLDocument) doc;
            int caretPos = target.getCaretPosition();

            try {
                Element paragraph = htmlDoc.getParagraphElement(caretPos);

                if (isListItem(paragraph)) {
                    doc.insertString(caretPos, "\n", null);
                } else {
                    insertLineBreak(target);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private boolean isListItem(Element element) {
            if (element == null) {
                return false;
            }
            HTML.Tag tag = (HTML.Tag) element.getAttributes().getAttribute(StyleConstants.NameAttribute);
            return HTML.Tag.LI.equals(tag);
        }

        private void insertLineBreak(JTextComponent target) {
            try {
                Document doc = target.getDocument();
                doc.insertString(target.getCaretPosition(), "\n", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Action[] getActions() {
        Action[] parentActions = super.getActions();
        Action[] customActions = new Action[] {
            new InsertBreakAction(),
            new InsertLineBreakAction()
        };

        Action[] allActions = new Action[parentActions.length + customActions.length];
        System.arraycopy(parentActions, 0, allActions, 0, parentActions.length);
        System.arraycopy(customActions, 0, allActions, parentActions.length, customActions.length);

        return allActions;
    }
}











