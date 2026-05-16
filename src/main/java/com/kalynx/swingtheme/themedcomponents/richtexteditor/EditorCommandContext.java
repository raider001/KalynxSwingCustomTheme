package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class EditorCommandContext {
    
    private final JTextComponent editor;
    private final JComponent editorPanel;
    
    public EditorCommandContext(JTextComponent editor, JComponent editorPanel) {
        this.editor = editor;
        this.editorPanel = editorPanel;
    }
    
    public JTextComponent getEditor() {
        return editor;
    }
    
    public JComponent getEditorPanel() {
        return editorPanel;
    }
    
    public Document getDocument() {
        return editor.getDocument();
    }
    
    public int getCaretPosition() {
        return editor.getCaretPosition();
    }
    
    public void setCaretPosition(int position) {
        editor.setCaretPosition(position);
    }
    
    public String getSelectedText() {
        return editor.getSelectedText();
    }
    
    public int getSelectionStart() {
        return editor.getSelectionStart();
    }
    
    public int getSelectionEnd() {
        return editor.getSelectionEnd();
    }
    
    public boolean hasSelection() {
        return editor.getSelectionStart() != editor.getSelectionEnd();
    }
    
    public void replaceSelection(String text) {
        editor.replaceSelection(text);
    }
    
    public boolean isEditable() {
        return editor.isEditable();
    }
    
    public String getText() {
        return editor.getText();
    }
    
    public void setText(String text) {
        editor.setText(text);
    }
}

