package com.kalynx.swingtheme.themedcomponents;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.CompactHTMLWriter;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.CustomHTMLEditorKit;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandContext;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.EditorCommandManager;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.FormatOption;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.RichTextEditorButton;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.BlockedFlashLayerUI;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.RichTextEditorCommand;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.commands.*;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.icons.*;
import com.kalynx.swingtheme.utils.HtmlThemeHelper;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ThemedRichTextEditor extends ThemedPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    private final JEditorPane editorPane;
    private final EditorCommandManager commandManager;
    private final ThemedPanel toolbarPanel;
    private final UndoManager undoManager;
    private final Map<String, RichTextEditorButton> buttonMap = new HashMap<>();
    private JComboBox<FormatOption> formatComboBox;
    private ActionListener formatComboListener;

    public ThemedRichTextEditor() {
        this(true);
    }

    public ThemedRichTextEditor(boolean showToolbar) {
        super(new BorderLayout());

        editorPane = new JEditorPane();
        editorPane.setContentType("text/html");

        CustomHTMLEditorKit kit = new CustomHTMLEditorKit();
        editorPane.setEditorKit(kit);

        HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
        updateEditorColors();
        updateStyleSheet(doc);

        installKeyBindings(editorPane, kit);

        undoManager = new UndoManager();
        doc.addUndoableEditListener(undoManager);

        EditorCommandContext context = new EditorCommandContext(editorPane, this);
        commandManager = new EditorCommandManager(context);
        commandManager.setPostExecuteHook(this::updateButtonStates);

        registerDefaultCommands();

        ThemedScrollPane scrollPane = new ThemedScrollPane(editorPane);
        add(scrollPane, BorderLayout.CENTER);

        if (showToolbar) {
            toolbarPanel = createToolbar();
            add(toolbarPanel, BorderLayout.NORTH);

            editorPane.addCaretListener(_ -> onCaretChanged());
        } else {
            toolbarPanel = null;
        }

        themeManager.addThemeChangeListener(this::onThemeChanged);
    }

    private void installKeyBindings(JEditorPane editorPane, CustomHTMLEditorKit kit) {
        InputMap inputMap = editorPane.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = editorPane.getActionMap();

        KeyStroke enterKey = KeyStroke.getKeyStroke("ENTER");
        KeyStroke shiftEnterKey = KeyStroke.getKeyStroke("shift ENTER");

        Action[] actions = kit.getActions();
        Action insertBreakAction = null;
        Action insertLineBreakAction = null;

        for (Action action : actions) {
            String name = (String) action.getValue(Action.NAME);
            if ("insert-break".equals(name)) {
                insertBreakAction = action;
            } else if ("insert-line-break".equals(name)) {
                insertLineBreakAction = action;
            }
        }

        if (insertBreakAction != null) {
            actionMap.put("insert-break", insertBreakAction);
            inputMap.put(enterKey, "insert-break");
        }

        if (insertLineBreakAction != null) {
            actionMap.put("insert-line-break", insertLineBreakAction);
            inputMap.put(shiftEnterKey, "insert-line-break");
        }
    }

    private void registerDefaultCommands() {
        commandManager.registerCommand("bold", new BoldCommand());
        commandManager.registerCommand("italic", new ItalicCommand());
        commandManager.registerCommand("underline", new UnderlineCommand());
        commandManager.registerCommand("orderedList", new OrderedListCommand());
        commandManager.registerCommand("unorderedList", new UnorderedListCommand());
        commandManager.registerCommand("indent", new IndentCommand());
        commandManager.registerCommand("outdent", new OutdentCommand());
        commandManager.registerCommand("undo", new UndoCommand(undoManager));
        commandManager.registerCommand("redo", new RedoCommand(undoManager));
        commandManager.registerCommand("cut", new CutCommand());
        commandManager.registerCommand("copy", new CopyCommand());
        commandManager.registerCommand("paste", new PasteCommand());
        commandManager.registerCommand("selectAll", new SelectAllCommand());
        commandManager.registerCommand("clearFormatting", new ClearFormattingCommand());
        commandManager.registerCommand("codeBlock", new CodeBlockCommand());

        commandManager.registerCommand("formatParagraph", new FormatCommand("Paragraph"));
        commandManager.registerCommand("formatH1", new FormatCommand("H1"));
        commandManager.registerCommand("formatH2", new FormatCommand("H2"));
        commandManager.registerCommand("formatH3", new FormatCommand("H3"));
        commandManager.registerCommand("formatH4", new FormatCommand("H4"));
        commandManager.registerCommand("formatH5", new FormatCommand("H5"));
    }

    private ThemedPanel createToolbar() {
        ThemedPanel toolbar = new ThemedPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, themeManager.scale(2), themeManager.scale(2)));

        int iconSize = themeManager.scale(20);
        int formatIconSize = themeManager.scale(16);

        FormatOption[] formatOptions = new FormatOption[] {
            new FormatOption("Normal", new NormalIcon(formatIconSize), "formatNormal"),
            new FormatOption("Paragraph", new ParagraphIcon(formatIconSize), "formatParagraph"),
            new FormatOption("H1", new HeadingIcon(formatIconSize, 1), "formatH1"),
            new FormatOption("H2", new HeadingIcon(formatIconSize, 2), "formatH2"),
            new FormatOption("H3", new HeadingIcon(formatIconSize, 3), "formatH3"),
            new FormatOption("H4", new HeadingIcon(formatIconSize, 4), "formatH4"),
            new FormatOption("H5", new HeadingIcon(formatIconSize, 5), "formatH5")
        };

        formatComboBox = new JComboBox<>(formatOptions);
        formatComboBox.setSelectedIndex(0);
        formatComboBox.setMaximumRowCount(6);
        formatComboBox.setRenderer(new FormatComboBoxRenderer());
        formatComboBox.setFont(new Font(themeManager.getBaseFontFamily(), Font.PLAIN, themeManager.scale(12)));
        formatComboBox.setPreferredSize(new Dimension(themeManager.scale(50), themeManager.scale(28)));

        Theme theme = themeManager.getCurrentTheme();
        formatComboBox.setBackground(theme.getInputBackground());
        formatComboBox.setForeground(theme.getForegroundColor());
        formatComboBox.setOpaque(true);
        formatComboBox.setFocusable(true);
        formatComboBox.setBorder(BorderFactory.createLineBorder(theme.getBorderColor(), 1));
        formatComboBox.setUI(new FormatComboBoxUI());

        BlockedFlashLayerUI formatComboFlashUI = new BlockedFlashLayerUI();
        formatComboListener = _ -> {
            FormatOption selected = (FormatOption) formatComboBox.getSelectedItem();
            if (selected == null) {
                return;
            }
            RichTextEditorCommand command = commandManager.getCommand(selected.getCommandId());
            EditorCommandContext context = commandManager.getContext();
            if (command != null && !command.isEnabled(context)) {
                formatComboFlashUI.flash();
                SwingUtilities.invokeLater(this::updateButtonStates);
                return;
            }

            commandManager.executeCommand(selected.getCommandId());
            editorPane.requestFocusInWindow();

            SwingUtilities.invokeLater(this::updateButtonStates);
        };
        formatComboBox.addActionListener(formatComboListener);

        JLayer<JComponent> wrappedCombo = new JLayer<>(formatComboBox, formatComboFlashUI);
        toolbar.add(wrappedCombo);
        toolbar.add(createSeparator());

        addToolbarButton(toolbar, new BoldIcon(iconSize), "bold", "Bold");
        addToolbarButton(toolbar, new ItalicIcon(iconSize), "italic", "Italic");
        addToolbarButton(toolbar, new UnderlineIcon(iconSize), "underline", "Underline");
        toolbar.add(createSeparator());

        addToolbarButton(toolbar, new UnorderedListIcon(iconSize), "unorderedList", "Bullet List");
        addToolbarButton(toolbar, new OrderedListIcon(iconSize), "orderedList", "Numbered List");
        addToolbarButton(toolbar, new IndentIcon(iconSize), "indent", "Increase Indent");
        addToolbarButton(toolbar, new OutdentIcon(iconSize), "outdent", "Decrease Indent");
        toolbar.add(createSeparator());

        addToolbarButton(toolbar, new ClearFormattingIcon(iconSize), "clearFormatting", "Clear Formatting");
        addToolbarButton(toolbar, new CodeBlockIcon(iconSize), "codeBlock", "Code Block");
        toolbar.add(createSeparator());

        addToolbarButton(toolbar, new UndoIcon(iconSize), "undo", "Undo");
        addToolbarButton(toolbar, new RedoIcon(iconSize), "redo", "Redo");

        return toolbar;
    }

    private void addToolbarButton(ThemedPanel toolbar, Icon icon, String commandId, String tooltip) {
        RichTextEditorButton button = createToolbarButton(icon, commandId, tooltip);
        toolbar.add(button);
        buttonMap.put(commandId, button);
    }

    private RichTextEditorButton createToolbarButton(Icon icon, String commandId, String tooltip) {
        RichTextEditorCommand command = commandManager.getCommand(commandId);
        RichTextEditorButton button = new RichTextEditorButton(icon);

        if (command != null) {
            String fullTooltip = command.getTooltip();
            button.setToolTipText(fullTooltip);
            button.addActionListener(_ -> {
                EditorCommandContext context = commandManager.getContext();
                if (!command.isEnabled(context)) {
                    button.flashBlocked();
                    return;
                }
                commandManager.executeCommand(commandId);
                editorPane.requestFocusInWindow();
            });
        } else {
            button.setToolTipText(tooltip);
        }

        return button;
    }

    private void onCaretChanged() {
        syncInputAttributesFromDocument();
        updateButtonStates();
    }

    private void updateButtonStates() {
        int pos = editorPane.getCaretPosition();
        Document doc = editorPane.getDocument();

        if (!(doc instanceof HTMLDocument htmlDoc)) {
            return;
        }

        if (pos < 0 || pos > doc.getLength()) {
            updateButtonState("bold", false);
            updateButtonState("italic", false);
            updateButtonState("underline", false);
            if (formatComboBox != null) {
                formatComboBox.setSelectedItem("Normal");
            }
            return;
        }

        boolean bold;
        boolean italic;
        boolean underline;

        if (editorPane.getSelectionStart() != editorPane.getSelectionEnd()) {
            int selStart = editorPane.getSelectionStart();
            bold = isFormattingActive(htmlDoc, selStart, StyleConstants.Bold);
            italic = isFormattingActive(htmlDoc, selStart, StyleConstants.Italic);
            underline = isFormattingActive(htmlDoc, selStart, StyleConstants.Underline);
        } else {
            MutableAttributeSet inputAttrs = resolveInputAttributes();
            if (inputAttrs != null) {
                bold = StyleConstants.isBold(inputAttrs);
                italic = StyleConstants.isItalic(inputAttrs);
                underline = StyleConstants.isUnderline(inputAttrs);
            } else {
                bold = isFormattingActive(htmlDoc, pos, StyleConstants.Bold);
                italic = isFormattingActive(htmlDoc, pos, StyleConstants.Italic);
                underline = isFormattingActive(htmlDoc, pos, StyleConstants.Underline);
            }
        }

        updateButtonState("bold", bold);
        updateButtonState("italic", italic);
        updateButtonState("underline", underline);

        updateFormatComboBox(htmlDoc, pos);
    }

    private void syncInputAttributesFromDocument() {
        int pos = editorPane.getCaretPosition();
        Document doc = editorPane.getDocument();
        if (!(doc instanceof HTMLDocument htmlDoc)) {
            return;
        }
        MutableAttributeSet inputAttrs = resolveInputAttributes();
        if (inputAttrs == null) {
            return;
        }
        StyleConstants.setBold(inputAttrs, isFormattingActive(htmlDoc, pos, StyleConstants.Bold));
        StyleConstants.setItalic(inputAttrs, isFormattingActive(htmlDoc, pos, StyleConstants.Italic));
        StyleConstants.setUnderline(inputAttrs, isFormattingActive(htmlDoc, pos, StyleConstants.Underline));
    }

    private boolean isFormattingActive(HTMLDocument doc, int pos, Object styleKey) {
        int lookupPos = pos > 0 ? pos - 1 : 0;
        Element element = doc.getCharacterElement(lookupPos);
        while (element != null) {
            AttributeSet attrs = element.getAttributes();
            Object tag = attrs.getAttribute(StyleConstants.NameAttribute);
            if (tag != null) {
                String tagName = tag.toString().toLowerCase();
                if (styleKey == StyleConstants.Bold && (tagName.equals("b") || tagName.equals("strong"))) return true;
                if (styleKey == StyleConstants.Italic && (tagName.equals("i") || tagName.equals("em"))) return true;
                if (styleKey == StyleConstants.Underline && tagName.equals("u")) return true;
            }
            if (styleKey == StyleConstants.Bold && StyleConstants.isBold(attrs)) return true;
            if (styleKey == StyleConstants.Italic && StyleConstants.isItalic(attrs)) return true;
            if (styleKey == StyleConstants.Underline && StyleConstants.isUnderline(attrs)) return true;
            element = element.getParentElement();
        }
        return false;
    }

    private MutableAttributeSet resolveInputAttributes() {
        if (editorPane.getEditorKit() instanceof StyledEditorKit styledKit) {
            return styledKit.getInputAttributes();
        }
        return null;
    }

    private void updateFormatComboBox(HTMLDocument htmlDoc, int pos) {
        if (formatComboBox == null) {
            return;
        }

        Element paragraph = htmlDoc.getParagraphElement(pos);
        AttributeSet attrs = paragraph.getAttributes();
        Object tag = attrs.getAttribute(StyleConstants.NameAttribute);

        String commandId = "formatParagraph";
        if (tag != null) {
            HTML.Tag htmlTag = (HTML.Tag) tag;
            if (htmlTag.equals(HTML.Tag.P)) {
                commandId = "formatParagraph";
            } else if (htmlTag.equals(HTML.Tag.H1)) {
                commandId = "formatH1";
            } else if (htmlTag.equals(HTML.Tag.H2)) {
                commandId = "formatH2";
            } else if (htmlTag.equals(HTML.Tag.H3)) {
                commandId = "formatH3";
            } else if (htmlTag.equals(HTML.Tag.H4)) {
                commandId = "formatH4";
            } else if (htmlTag.equals(HTML.Tag.H5)) {
                commandId = "formatH5";
            }
        }

        FormatOption currentSelection = (FormatOption) formatComboBox.getSelectedItem();
        if (currentSelection == null || !currentSelection.getCommandId().equals(commandId)) {
            formatComboBox.removeActionListener(formatComboListener);
            try {
                ComboBoxModel<FormatOption> model = formatComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    FormatOption option = model.getElementAt(i);
                    if (option != null && option.getCommandId().equals(commandId)) {
                        formatComboBox.setSelectedItem(option);
                        break;
                    }
                }
            } finally {
                formatComboBox.addActionListener(formatComboListener);
            }
        }
    }

    private void updateButtonState(String commandId, boolean active) {
        RichTextEditorButton button = buttonMap.get(commandId);
        if (button != null) {
            button.setActive(active);
        }
    }

    private Component createSeparator() {
        JPanel separator = new JPanel();
        separator.setOpaque(false);
        separator.setPreferredSize(new Dimension(themeManager.scale(8), themeManager.scale(32)));
        return separator;
    }

    private void onThemeChanged() {
        SwingUtilities.invokeLater(() -> {
            HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
            updateStyleSheet(doc);
            updateEditorColors();

            if (formatComboBox != null) {
                Theme theme = themeManager.getCurrentTheme();
                formatComboBox.setBackground(theme.getInputBackground());
                formatComboBox.setForeground(theme.getForegroundColor());
                formatComboBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(theme.getBorderColor(), 1),
                    BorderFactory.createEmptyBorder(
                        themeManager.scale(3),
                        themeManager.scale(6),
                        themeManager.scale(3),
                        themeManager.scale(6)
                    )
                ));
                formatComboBox.repaint();
            }

            if (toolbarPanel != null) {
                toolbarPanel.revalidate();
                toolbarPanel.repaint();
            }

            revalidate();
            repaint();
        });
    }

    private void updateStyleSheet(HTMLDocument doc) {
        Theme theme = themeManager.getCurrentTheme();
        StyleSheet documentStyleSheet = doc.getStyleSheet();

        int baseFontSize = themeManager.getBaseFontSize();
        String fontFamily = themeManager.getBaseFontFamily();
        HtmlThemeHelper.applyEditorStyleRules(documentStyleSheet, theme, baseFontSize, fontFamily);
    }

    private void updateEditorColors() {
        Theme theme = themeManager.getCurrentTheme();
        editorPane.setBackground(theme.getInputBackground());
        editorPane.setForeground(theme.getForegroundColor());
        editorPane.setCaretColor(theme.getAccentColor());
        editorPane.setSelectionColor(theme.getAccentColor());
    }


    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public EditorCommandManager getCommandManager() {
        return commandManager;
    }

    public String getHtml() {
        try {
            HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
            StringWriter writer = new StringWriter();
            CompactHTMLWriter htmlWriter = new CompactHTMLWriter(writer, doc);
            htmlWriter.write();
            return writer.toString();
        } catch (Exception e) {
            return editorPane.getText();
        }
    }

    public void setHtml(String html) {
        String incoming = html != null ? html : "";
        if (incoming.equals(getHtml())) {
            return;
        }
        int caretPos = editorPane.getCaretPosition();
        editorPane.setText(incoming);
        int length = editorPane.getDocument().getLength();
        try {
            editorPane.setCaretPosition(Math.min(caretPos, length));
        } catch (IllegalArgumentException ignored) {
        }
    }

    public String getPlainText() {
        try {
            return editorPane.getDocument().getText(0, editorPane.getDocument().getLength());
        } catch (Exception e) {
            return "";
        }
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }


    private class FormatComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼") {
                @Override
                public void paintComponent(Graphics g) {
                    Theme theme = themeManager.getCurrentTheme();

                    g.setColor(theme.getInputBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());

                    g.setColor(theme.getForegroundColor());
                    g.setFont(new Font("Segoe UI", Font.PLAIN, themeManager.scale(10)));
                    FontMetrics fm = g.getFontMetrics();
                    String text = getText();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g.drawString(text, x, y);
                }
            };

            button.setName("ComboBox.arrowButton");
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setOpaque(false);
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Theme theme = themeManager.getCurrentTheme();
            g.setColor(theme.getInputBackground());
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private class FormatComboBoxRenderer extends DefaultListCellRenderer {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof FormatOption option) {
                setText("");
                setIcon(option.getIcon());
                setHorizontalAlignment(CENTER);
            }

            Theme theme = themeManager.getCurrentTheme();
            if (isSelected) {
                setBackground(theme.getAccentColor());
                setForeground(theme.getBackgroundColor());
            } else {
                setBackground(theme.getInputBackground());
                setForeground(theme.getForegroundColor());
            }

            setBorder(BorderFactory.createEmptyBorder(
                themeManager.scale(4),
                themeManager.scale(6),
                themeManager.scale(4),
                themeManager.scale(6)
            ));
            setOpaque(true);
            return this;
        }
    }
}






























































