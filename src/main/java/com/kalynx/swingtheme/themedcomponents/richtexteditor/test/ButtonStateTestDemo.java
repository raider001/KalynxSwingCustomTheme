package com.kalynx.swingtheme.themedcomponents.richtexteditor.test;

import com.kalynx.swingtheme.theme.ThemeManager;
import com.kalynx.swingtheme.themedcomponents.ThemedFrame;
import com.kalynx.swingtheme.themedcomponents.ThemedRichTextEditor;

import javax.swing.*;
import java.awt.*;

public class ButtonStateTestDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ThemedFrame frame = new ThemedFrame("Button State Test");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            ThemedRichTextEditor editor = new ThemedRichTextEditor(true);

            String testHtml = "<p>Regular text <b>bold text</b> <i>italic text</i> <u>underlined text</u></p>" +
                             "<p><b><i>Bold and italic</i></b> text</p>" +
                             "<p>Test: Select some text and click Bold, then move cursor into that text.</p>";

            editor.setHtml(testHtml);

            frame.setContentPane(editor);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            System.out.println("Button State Test Demo started");
            System.out.println("Instructions:");
            System.out.println("1. Click in the bold text - Bold button should highlight");
            System.out.println("2. Click in the italic text - Italic button should highlight");
            System.out.println("3. Click in the underlined text - Underline button should highlight");
            System.out.println("4. Select text and click Bold - it should toggle bold");
            System.out.println("5. Put cursor in newly formatted text - button should highlight");
        });
    }
}

