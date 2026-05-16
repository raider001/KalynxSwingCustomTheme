package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.KeyEvent;
import java.io.File;

public class InsertImageCommand extends AbstractEditorCommand {

    public InsertImageCommand() {
        super("Insert Image", null, CommandCategory.INSERT,
              KeyCombination.ctrlShift(KeyEvent.VK_I));
    }

    @Override
    public void execute(EditorCommandContext context) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "jpg", "jpeg", "png", "gif", "bmp"
        ));

        int result = fileChooser.showOpenDialog(context.getEditorPanel());

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.toURI().toString();

            String html = "<img src=\"" + imagePath + "\" alt=\"Image\" />";

            if (context.getDocument() instanceof HTMLDocument && context.getEditor() instanceof JEditorPane) {
                HTMLDocument doc = (HTMLDocument) context.getDocument();
                JEditorPane editorPane = (JEditorPane) context.getEditor();
                HTMLEditorKit kit = (HTMLEditorKit) editorPane.getEditorKit();

                try {
                    int pos = context.getCaretPosition();
                    kit.insertHTML(doc, pos, html, 0, 0, HTML.Tag.IMG);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}


