package com.kalynx.swingtheme.themedcomponents.richtexteditor.demo;

import com.kalynx.swingtheme.themedcomponents.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;

public class RichTextEditorDemo {

    private static ThemedTextArea htmlOutputArea;
    private static ThemedRichTextEditor editor;

    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ThemedFrame frame = new ThemedFrame("Rich Text Editor Demo");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);

            ThemedPanel mainPanel = new ThemedPanel(new BorderLayout());

            ThemedPanel topPanel = new ThemedPanel(new MigLayout("", "[][][]push[]", ""));

            ThemedLabel titleLabel = new ThemedLabel("Rich Text Editor with List Support & HTML Preview");
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
            topPanel.add(titleLabel, "span, wrap");

            ThemedButton loadSampleButton = new ThemedButton("Load Sample");
            loadSampleButton.addActionListener(_ -> loadSampleContent());
            topPanel.add(loadSampleButton, "");

            ThemedButton clearButton = new ThemedButton("Clear");
            clearButton.addActionListener(_ -> {
                editor.setHtml("");
                updateHtmlOutput();
            });
            topPanel.add(clearButton, "");

            ThemedButton exportUndoButton = new ThemedButton("Export Undo History");
            exportUndoButton.addActionListener(_ -> exportUndoHistory());
            topPanel.add(exportUndoButton, "wrap");

            mainPanel.add(topPanel, BorderLayout.NORTH);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setResizeWeight(0.6);
            splitPane.setDividerLocation(0.6);

            editor = new ThemedRichTextEditor(true);
            editor.getEditorPane().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    SwingUtilities.invokeLater(RichTextEditorDemo::updateHtmlOutput);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    SwingUtilities.invokeLater(RichTextEditorDemo::updateHtmlOutput);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    SwingUtilities.invokeLater(RichTextEditorDemo::updateHtmlOutput);
                }
            });

            loadSampleContent();
            splitPane.setLeftComponent(editor);

            ThemedPanel rightPanel = new ThemedPanel(new BorderLayout());
            ThemedLabel htmlLabel = new ThemedLabel("Raw HTML Output:");
            htmlLabel.setFont(htmlLabel.getFont().deriveFont(Font.BOLD, 14f));
            ThemedPanel htmlLabelPanel = new ThemedPanel(new MigLayout("", "[grow]", ""));
            htmlLabelPanel.add(htmlLabel, "grow");
            rightPanel.add(htmlLabelPanel, BorderLayout.NORTH);

            htmlOutputArea = new ThemedTextArea();
            htmlOutputArea.setEditable(false);
            htmlOutputArea.setLineWrap(true);
            htmlOutputArea.setWrapStyleWord(false);
            htmlOutputArea.setFont(new Font("Consolas", Font.PLAIN, 12));

            ThemedScrollPane htmlScrollPane = new ThemedScrollPane(htmlOutputArea);
            rightPanel.add(htmlScrollPane, BorderLayout.CENTER);

            splitPane.setRightComponent(rightPanel);
            mainPanel.add(splitPane, BorderLayout.CENTER);

            ThemedPanel infoPanel = new ThemedPanel(new MigLayout("", "[grow]", ""));
            ThemedLabel infoLabel = new ThemedLabel(
                "<html><b>Keyboard Shortcuts:</b> " +
                "Ctrl+B: Bold | Ctrl+I: Italic | Ctrl+U: Underline | " +
                "Ctrl+Shift+O: Ordered List | Ctrl+Shift+U: Unordered List | " +
                "Tab: Indent | Shift+Tab: Outdent | " +
                "Ctrl+Z: Undo | Ctrl+Y: Redo | " +
                "Ctrl+A: Select All | Ctrl+X/C/V: Cut/Copy/Paste</html>"
            );
            infoPanel.add(infoLabel, "grow");
            mainPanel.add(infoPanel, BorderLayout.SOUTH);

            frame.getContentPanel().add(mainPanel, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            updateHtmlOutput();
        });
    }

    private static void updateHtmlOutput() {
        if (editor != null && htmlOutputArea != null) {
            String html = stripStyle(editor.getHtml());
            String formattedHtml = formatHtml(html);
            htmlOutputArea.setText(formattedHtml);
            htmlOutputArea.setCaretPosition(0);
        }
    }

    private static String stripStyle(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        return html.replaceAll("(?is)<style[^>]*>.*?</style>\\s*", "");
    }

    private static String formatHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }

        StringBuilder formatted = new StringBuilder();
        int indent = 0;
        boolean inTag = false;
        boolean closingTag = false;

        for (int i = 0; i < html.length(); i++) {
            char c = html.charAt(i);

            if (c == '<') {
                if (!inTag && !formatted.isEmpty() && formatted.charAt(formatted.length() - 1) != '\n') {
                    formatted.append('\n');
                    formatted.append("  ".repeat(Math.max(0, indent)));
                }
                inTag = true;
                closingTag = (i + 1 < html.length() && html.charAt(i + 1) == '/');
                if (closingTag) {
                    indent = Math.max(0, indent - 1);
                    formatted.setLength(Math.max(0, formatted.length() - 2));
                }
                formatted.append(c);
            } else if (c == '>') {
                formatted.append(c);
                inTag = false;
                if (!closingTag && i > 0) {
                    String tag = getTagName(html, i);
                    if (!isSelfClosingTag(tag)) {
                        indent++;
                    }
                }
            } else {
                formatted.append(c);
            }
        }

        return formatted.toString();
    }

    private static String getTagName(String html, int closePos) {
        int openPos = html.lastIndexOf('<', closePos);
        if (openPos == -1) return "";
        String tag = html.substring(openPos + 1, closePos);
        if (tag.startsWith("/")) tag = tag.substring(1);
        int spacePos = tag.indexOf(' ');
        if (spacePos > 0) tag = tag.substring(0, spacePos);
        return tag.toLowerCase();
    }

    private static boolean isSelfClosingTag(String tag) {
        return tag.equals("br") || tag.equals("img") || tag.equals("hr") ||
               tag.equals("input") || tag.equals("meta") || tag.equals("link");
    }

    private static void exportUndoHistory() {
        if (editor == null) return;

        UndoManager undoManager = editor.getUndoManager();

        StringBuilder history = new StringBuilder();
        history.append("=== Undo History Export ===\n");
        history.append("Timestamp: ").append(System.currentTimeMillis()).append("\n");
        history.append("Can Undo: ").append(undoManager.canUndo()).append("\n");
        history.append("Can Redo: ").append(undoManager.canRedo()).append("\n");
        history.append("Undo Presentation Name: ").append(undoManager.getUndoPresentationName()).append("\n");
        history.append("Redo Presentation Name: ").append(undoManager.getRedoPresentationName()).append("\n\n");

        history.append("=== Current Document State ===\n");
        history.append("HTML Length: ").append(editor.getHtml().length()).append(" characters\n");
        history.append("Plain Text Length: ").append(editor.getPlainText().length()).append(" characters\n\n");

        history.append("=== HTML Content ===\n");
        history.append(editor.getHtml()).append("\n\n");

        history.append("=== Plain Text Content ===\n");
        history.append(editor.getPlainText()).append("\n");

        try {
            File file = new File("undo_history_" + System.currentTimeMillis() + ".txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(history.toString());
            }

            JOptionPane.showMessageDialog(
                null,
                "Undo history exported to:\n" + file.getAbsolutePath(),
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                null,
                "Failed to export undo history:\n" + ex.getMessage(),
                "Export Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void loadSampleContent() {
        String sampleHtml =
            "<h1>Welcome to the Rich Text Editor</h1>" +
            "<p>This editor demonstrates <b>bold</b>, <i>italic</i>, and <u>underlined</u> text formatting.</p>" +

            "<h2>Unordered Lists</h2>" +
            "<p>Here's a shopping list:</p>" +
            "<ul>" +
            "<li>Fresh vegetables</li>" +
            "<li>Organic fruits</li>" +
            "<li>Whole grain bread</li>" +
            "<li>Dairy products" +
            "  <ul>" +
            "    <li>Milk</li>" +
            "    <li>Cheese</li>" +
            "    <li>Yogurt</li>" +
            "  </ul>" +
            "</li>" +
            "</ul>" +

            "<h2>Ordered Lists</h2>" +
            "<p>Steps to make coffee:</p>" +
            "<ol>" +
            "<li>Boil water to 195-205°F</li>" +
            "<li>Grind fresh coffee beans" +
            "  <ol>" +
            "    <li>Use medium grind for drip coffee</li>" +
            "    <li>Use fine grind for espresso</li>" +
            "  </ol>" +
            "</li>" +
            "<li>Add coffee to filter</li>" +
            "<li>Pour water slowly over grounds</li>" +
            "<li>Wait 4 minutes</li>" +
            "<li>Enjoy your perfect cup!</li>" +
            "</ol>" +

            "<h2>Mixed Content</h2>" +
            "<p>You can combine different formatting:</p>" +
            "<ul>" +
            "<li><b>Bold text</b> in a list</li>" +
            "<li><i>Italic text</i> for emphasis</li>" +
            "<li><u>Underlined text</u> for importance</li>" +
            "<li>And even <b><i><u>all three at once!</u></i></b></li>" +
            "</ul>" +

            "<h2>Features</h2>" +
            "<ol>" +
            "<li><b>Theme Support</b>: Switch between light and dark themes dynamically</li>" +
            "<li><b>List Management</b>: Create ordered and unordered lists easily</li>" +
            "<li><b>Indentation</b>: Use Tab/Shift+Tab to indent and outdent</li>" +
            "<li><b>Formatting</b>: Apply bold, italic, and underline to selected text</li>" +
            "<li><b>Commands</b>: All actions are command-based for easy extension</li>" +
            "</ol>" +

            "<p>Try selecting text and using the toolbar buttons or keyboard shortcuts!</p>";

        editor.setHtml(sampleHtml);
        updateHtmlOutput();
    }
}


