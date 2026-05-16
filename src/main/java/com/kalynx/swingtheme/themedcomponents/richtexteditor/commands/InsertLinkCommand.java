package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.KeyEvent;

public class InsertLinkCommand extends AbstractEditorCommand {

    public InsertLinkCommand() {
        super("Insert Link", null, CommandCategory.INSERT,
              KeyCombination.ctrl(KeyEvent.VK_K));
    }

    @Override
    public void execute(EditorCommandContext context) {
        String selectedText = context.getSelectedText();
        String displayText = selectedText != null && !selectedText.isEmpty()
            ? selectedText
            : "Link Text";

        String url = JOptionPane.showInputDialog(
            context.getEditorPanel(),
            "Enter URL:",
            "Insert Link",
            JOptionPane.PLAIN_MESSAGE
        );

        if (url != null && !url.trim().isEmpty()) {
            String html = "<a href=\"" + url + "\">" + displayText + "</a>";

            if (context.getDocument() instanceof HTMLDocument && context.getEditor() instanceof JEditorPane) {
                HTMLDocument doc = (HTMLDocument) context.getDocument();
                JEditorPane editorPane = (JEditorPane) context.getEditor();
                HTMLEditorKit kit = (HTMLEditorKit) editorPane.getEditorKit();

                try {
                    int pos = context.getSelectionStart();
                    if (context.hasSelection()) {
                        doc.remove(pos, context.getSelectionEnd() - pos);
                    }
                    kit.insertHTML(doc, pos, html, 0, 0, HTML.Tag.A);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                context.replaceSelection(displayText);
            }
        }
    }
}


