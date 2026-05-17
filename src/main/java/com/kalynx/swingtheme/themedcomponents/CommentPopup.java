package com.kalynx.swingtheme.themedcomponents;

import java.io.Serial;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

@SuppressWarnings("unused")
public class CommentPopup extends ThemedPopupDialog {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private transient final ThemedRichTextEditor commentEditor;
    private transient ActionListener submitCallback;

    public CommentPopup(Component parent, String initialText) {
        super(parent, "Add Comment");
        ThemeManager themeManager = ThemeManager.getInstance();

        setDialogSize(600, 450);

        ThemedPanel mainPanel = (ThemedPanel) getContentPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));

        Theme theme = themeManager.getCurrentTheme();

        ThemedLabel label = new ThemedLabel("Comment:");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        mainPanel.add(label, BorderLayout.NORTH);

        commentEditor = new ThemedRichTextEditor();
        commentEditor.setHtml(initialText);
        mainPanel.add(commentEditor, BorderLayout.CENTER);

        ThemedPanel buttonPanel = new ThemedPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        ThemedButton cancelButton = new ThemedButton("Cancel");
        cancelButton.addActionListener(this::dispose);

        ThemedButton submitButton = new ThemedButton("Add Comment");
        submitButton.addActionListener(() -> {
            if (submitCallback != null) {
                submitCallback.actionPerformed(null);
            }
        });

        InputMap inputMap = commentEditor.getEditorPane().getInputMap();
        ActionMap actionMap = commentEditor.getEditorPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        actionMap.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "submit");
        actionMap.put("submit", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (submitCallback != null) {
                    submitCallback.actionPerformed(null);
                }
                dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        commentEditor.getEditorPane().requestFocusInWindow();
    }

    public String getComment() {
        return commentEditor.getHtml();
    }

    public void setSubmitCallback(ActionListener callback) {
        this.submitCallback = callback;
    }
}

