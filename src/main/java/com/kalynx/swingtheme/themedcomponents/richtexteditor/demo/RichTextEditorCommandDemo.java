package com.kalynx.swingtheme.themedcomponents.richtexteditor.demo;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.*;
import com.kalynx.swingtheme.themedcomponents.richtexteditor.commands.*;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class RichTextEditorCommandDemo extends JFrame {

    private final JEditorPane editorPane;
    private final EditorCommandManager commandManager;
    private final UndoManager undoManager;
    private final JPanel mainPanel;

    public RichTextEditorCommandDemo() {
        super("Rich Text Editor with Command Framework");

        setLayout(new BorderLayout());
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());

        editorPane = new JEditorPane();
        HTMLEditorKit editorKit = new HTMLEditorKit();
        editorPane.setEditorKit(editorKit);
        editorPane.setDocument(editorKit.createDefaultDocument());

        undoManager = new UndoManager();
        editorPane.getDocument().addUndoableEditListener(undoManager);

        EditorCommandContext context = new EditorCommandContext(editorPane, mainPanel);
        commandManager = new EditorCommandManager(context);

        registerCommands();

        mainPanel.add(createToolbar(), BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(editorPane), BorderLayout.CENTER);
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        editorPane.setText("<html><body><h1>Rich Text Editor Demo</h1><p>Try using keyboard shortcuts!</p></body></html>");
    }

    private void registerCommands() {
        commandManager.registerCommand("bold", new BoldCommand());
        commandManager.registerCommand("italic", new ItalicCommand());
        commandManager.registerCommand("underline", new UnderlineCommand());
        commandManager.registerCommand("undo", new UndoCommand(undoManager));
        commandManager.registerCommand("redo", new RedoCommand(undoManager));
        commandManager.registerCommand("cut", new CutCommand());
        commandManager.registerCommand("copy", new CopyCommand());
        commandManager.registerCommand("paste", new PasteCommand());
        commandManager.registerCommand("selectAll", new SelectAllCommand());
    }

    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        Map<RichTextEditorCommand.CommandCategory, List<RichTextEditorCommand>> commandsByCategory =
            commandManager.getCommandsByCategory();

        addCommandButtons(toolbar, commandsByCategory.get(RichTextEditorCommand.CommandCategory.FORMATTING), "Formatting");
        toolbar.addSeparator();
        addCommandButtons(toolbar, commandsByCategory.get(RichTextEditorCommand.CommandCategory.EDITING), "Editing");
        toolbar.addSeparator();
        addCommandButtons(toolbar, commandsByCategory.get(RichTextEditorCommand.CommandCategory.UNDO_REDO), "Undo/Redo");

        return toolbar;
    }

    private void addCommandButtons(JToolBar toolbar, List<RichTextEditorCommand> commands, String groupName) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        for (RichTextEditorCommand command : commands) {
            JButton button = new JButton(command.getTitle());
            button.setIcon(command.getIcon());
            button.setToolTipText(command.getTooltip());

            button.addActionListener(e -> {
                EditorCommandContext context = new EditorCommandContext(editorPane, mainPanel);
                if (command.isEnabled(context)) {
                    command.execute(context);
                    editorPane.requestFocusInWindow();
                }
            });

            toolbar.add(button);
        }
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel statusLabel = new JLabel("Use keyboard shortcuts or toolbar buttons");
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JButton showShortcutsButton = new JButton("Show Shortcuts");
        showShortcutsButton.addActionListener(e -> showShortcutsDialog());
        statusPanel.add(showShortcutsButton, BorderLayout.EAST);

        return statusPanel;
    }

    private void showShortcutsDialog() {
        StringBuilder shortcuts = new StringBuilder();
        shortcuts.append("<html><body style='padding: 10px;'>");
        shortcuts.append("<h2>Keyboard Shortcuts</h2>");

        Map<RichTextEditorCommand.CommandCategory, List<RichTextEditorCommand>> commandsByCategory =
            commandManager.getCommandsByCategory();

        for (Map.Entry<RichTextEditorCommand.CommandCategory, List<RichTextEditorCommand>> entry : commandsByCategory.entrySet()) {
            shortcuts.append("<h3>").append(entry.getKey()).append("</h3>");
            shortcuts.append("<table cellpadding='5'>");

            for (RichTextEditorCommand command : entry.getValue()) {
                KeyCombination[] keys = command.getKeyCombinations();
                if (keys.length > 0) {
                    shortcuts.append("<tr>");
                    shortcuts.append("<td><b>").append(command.getTitle()).append(":</b></td>");
                    shortcuts.append("<td>");
                    for (int i = 0; i < keys.length; i++) {
                        if (i > 0) shortcuts.append(", ");
                        shortcuts.append(keys[i].toDisplayString());
                    }
                    shortcuts.append("</td>");
                    shortcuts.append("</tr>");
                }
            }

            shortcuts.append("</table>");
        }

        shortcuts.append("</body></html>");

        JEditorPane helpPane = new JEditorPane("text/html", shortcuts.toString());
        helpPane.setEditable(false);
        helpPane.setPreferredSize(new Dimension(500, 400));

        JScrollPane scrollPane = new JScrollPane(helpPane);
        JOptionPane.showMessageDialog(this, scrollPane, "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            RichTextEditorCommandDemo demo = new RichTextEditorCommandDemo();
            demo.setLocationRelativeTo(null);
            demo.setVisible(true);
        });
    }
}



