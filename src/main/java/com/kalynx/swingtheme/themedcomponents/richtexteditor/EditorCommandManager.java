package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EditorCommandManager {

    private final Map<String, RichTextEditorCommand> commands = new ConcurrentHashMap<>();
    private final Map<KeyCombination, RichTextEditorCommand> keyBindings = new ConcurrentHashMap<>();
    private final EditorCommandContext context;

    public EditorCommandManager(EditorCommandContext context) {
        this.context = context;
    }

    public void registerCommand(String id, RichTextEditorCommand command) {
        commands.put(id, command);

        for (KeyCombination keyCombination : command.getKeyCombinations()) {
            keyBindings.put(keyCombination, command);
            installKeyBinding(keyCombination, command);
        }

        EnterKeyDelegate delegate = command.getEnterKeyDelegate();
        if (delegate != null) {
            CustomHTMLEditorKit.registerEnterKeyDelegate(delegate);
        }
    }

    public void unregisterCommand(String id) {
        RichTextEditorCommand command = commands.remove(id);
        if (command != null) {
            for (KeyCombination keyCombination : command.getKeyCombinations()) {
                keyBindings.remove(keyCombination);
                uninstallKeyBinding(keyCombination);
            }
        }
    }

    public RichTextEditorCommand getCommand(String id) {
        return commands.get(id);
    }

    public RichTextEditorCommand getCommandForKey(KeyCombination keyCombination) {
        return keyBindings.get(keyCombination);
    }

    public Collection<RichTextEditorCommand> getAllCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    public Map<RichTextEditorCommand.CommandCategory, List<RichTextEditorCommand>> getCommandsByCategory() {
        Map<RichTextEditorCommand.CommandCategory, List<RichTextEditorCommand>> result = new HashMap<>();

        for (RichTextEditorCommand command : commands.values()) {
            result.computeIfAbsent(command.getCategory(), k -> new ArrayList<>()).add(command);
        }

        return result;
    }

    public void executeCommand(String id) {
        RichTextEditorCommand command = commands.get(id);
        if (command != null && command.isEnabled(context)) {
            command.execute(context);
        }
    }

    private void installKeyBinding(KeyCombination keyCombination, RichTextEditorCommand command) {
        JComponent component = context.getEditor();
        KeyStroke keyStroke = keyCombination.toKeyStroke();
        String actionKey = "command_" + command.getTitle() + "_" + keyCombination.hashCode();

        component.getInputMap(JComponent.WHEN_FOCUSED).put(keyStroke, actionKey);
        component.getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (command.isEnabled(context)) {
                    command.execute(context);
                }
            }
        });
    }

    private void uninstallKeyBinding(KeyCombination keyCombination) {
        JComponent component = context.getEditor();
        KeyStroke keyStroke = keyCombination.toKeyStroke();
        component.getInputMap(JComponent.WHEN_FOCUSED).remove(keyStroke);
    }
}


