package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import com.kalynx.swingtheme.themedcomponents.ThemedRichTextEditor;
import com.kalynx.swingtheme.theme.ThemeManager;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class ThemedRichTextEditorTest {

    @Test
    void testEditorInitialization() {
        SwingUtilities.invokeLater(() -> {
            ThemedRichTextEditor editor = new ThemedRichTextEditor();
            assertNotNull(editor.getEditorPane());
            assertNotNull(editor.getCommandManager());
        });
    }

    @Test
    void testHtmlContentGetSet() {
        SwingUtilities.invokeLater(() -> {
            ThemedRichTextEditor editor = new ThemedRichTextEditor();
            String testHtml = "<p>Hello <b>World</b></p>";
            editor.setHtml(testHtml);

            String retrievedHtml = editor.getHtml();
            assertTrue(retrievedHtml.contains("Hello"));
            assertTrue(retrievedHtml.contains("World"));
        });
    }

    @Test
    void testCommandRegistration() {
        SwingUtilities.invokeLater(() -> {
            ThemedRichTextEditor editor = new ThemedRichTextEditor();
            EditorCommandManager commandManager = editor.getCommandManager();

            assertNotNull(commandManager.getCommand("bold"));
            assertNotNull(commandManager.getCommand("italic"));
            assertNotNull(commandManager.getCommand("underline"));
            assertNotNull(commandManager.getCommand("orderedList"));
            assertNotNull(commandManager.getCommand("unorderedList"));
            assertNotNull(commandManager.getCommand("indent"));
            assertNotNull(commandManager.getCommand("outdent"));
            assertNotNull(commandManager.getCommand("undo"));
            assertNotNull(commandManager.getCommand("redo"));
        });
    }

    @Test
    void testThemeSupport() {
        SwingUtilities.invokeLater(() -> {
            ThemeManager themeManager = ThemeManager.getInstance();
            ThemedRichTextEditor editor = new ThemedRichTextEditor();

            themeManager.setDarkTheme();
            assertEquals("Dark", themeManager.getCurrentTheme().getName());

            themeManager.setLightTheme();
            assertEquals("Light", themeManager.getCurrentTheme().getName());
        });
    }

    @Test
    void testListCommands() {
        SwingUtilities.invokeLater(() -> {
            ThemedRichTextEditor editor = new ThemedRichTextEditor();
            EditorCommandManager commandManager = editor.getCommandManager();

            RichTextEditorCommand orderedList = commandManager.getCommand("orderedList");
            assertEquals("Ordered List", orderedList.getTitle());
            assertEquals(RichTextEditorCommand.CommandCategory.FORMATTING, orderedList.getCategory());

            RichTextEditorCommand unorderedList = commandManager.getCommand("unorderedList");
            assertEquals("Unordered List", unorderedList.getTitle());
            assertEquals(RichTextEditorCommand.CommandCategory.FORMATTING, unorderedList.getCategory());
        });
    }

    @Test
    void testIndentOutdentCommands() {
        SwingUtilities.invokeLater(() -> {
            ThemedRichTextEditor editor = new ThemedRichTextEditor();
            EditorCommandManager commandManager = editor.getCommandManager();

            RichTextEditorCommand indent = commandManager.getCommand("indent");
            assertNotNull(indent);
            assertEquals("Indent", indent.getTitle());

            RichTextEditorCommand outdent = commandManager.getCommand("outdent");
            assertNotNull(outdent);
            assertEquals("Outdent", outdent.getTitle());
        });
    }
}


