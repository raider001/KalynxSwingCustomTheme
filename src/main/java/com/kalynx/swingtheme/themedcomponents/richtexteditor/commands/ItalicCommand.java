package com.kalynx.swingtheme.themedcomponents.richtexteditor.commands;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.AbstractEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.KeyCombination;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.KeyEvent;

/**
 * Toggles italic formatting. When a selection is active the whole range is
 * toggled based on the italic state at the selection start; with no selection
 * the editor kit's input attributes are toggled so subsequently typed
 * characters inherit the new state.
 */
public class ItalicCommand extends AbstractEditorCommand {

    private enum Mode {
        SELECTION,
        NO_SELECTION
    }

    public ItalicCommand() {
        super("Italic", null, CommandCategory.FORMATTING,
              KeyCombination.ctrl(KeyEvent.VK_I));
    }

    @Override
    public void execute(EditorCommandContext context) {
        Mode mode = context.hasSelection() ? Mode.SELECTION : Mode.NO_SELECTION;
        switch (mode) {
            case SELECTION -> handleSelectionMode(context);
            case NO_SELECTION -> handleUnselectedMode(context);
        }
    }

    private void handleSelectionMode(EditorCommandContext context) {
        if (!(context.getDocument() instanceof StyledDocument styledDoc)) {
            return;
        }
        int start = context.getSelectionStart();
        int end = context.getSelectionEnd();

        Element element = styledDoc.getCharacterElement(start);
        boolean currentlyItalic = StyleConstants.isItalic(element.getAttributes());

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setItalic(attrs, !currentlyItalic);
        styledDoc.setCharacterAttributes(start, end - start, attrs, false);
    }

    private void handleUnselectedMode(EditorCommandContext context) {
        MutableAttributeSet inputAttrs = inputAttributes(context);
        if (inputAttrs == null) {
            return;
        }
        boolean currentlyItalic = StyleConstants.isItalic(inputAttrs);
        StyleConstants.setItalic(inputAttrs, !currentlyItalic);
    }

    private MutableAttributeSet inputAttributes(EditorCommandContext context) {
        if (!(context.getEditor() instanceof JEditorPane editorPane)) {
            return null;
        }
        if (!(editorPane.getEditorKit() instanceof StyledEditorKit styledKit)) {
            return null;
        }
        return styledKit.getInputAttributes();
    }
}
