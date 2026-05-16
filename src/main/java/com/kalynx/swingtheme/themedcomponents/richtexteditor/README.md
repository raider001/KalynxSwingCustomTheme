# Rich Text Editor Command Framework

A flexible, extensible command framework for building rich text editors in Swing applications.

## Overview

This framework provides a clean separation between UI components and editor actions through a command pattern implementation. Commands can be triggered via:
- Toolbar buttons
- Menu items
- Keyboard shortcuts
- Programmatic invocation

## Core Components

### RichTextEditorCommand Interface

The main interface that all commands must implement:

```java
public interface RichTextEditorCommand {
    String getTitle();                                    // Display name
    Icon getIcon();                                       // Optional icon
    KeyCombination[] getKeyCombinations();                // Keyboard shortcuts
    String getTooltip();                                  // Tooltip with shortcut info
    boolean isEnabled(EditorCommandContext context);      // Enable/disable logic
    void execute(EditorCommandContext context);           // Command action
    CommandCategory getCategory();                        // Category for organization
}
```

### EditorCommandContext

Provides access to the editor state and operations:

```java
EditorCommandContext context = new EditorCommandContext(editorPane, parentPanel);

// Query state
String selectedText = context.getSelectedText();
boolean hasSelection = context.hasSelection();
int caretPos = context.getCaretPosition();

// Perform operations
context.replaceSelection("New text");
context.setCaretPosition(100);
```

### KeyCombination

Represents keyboard shortcuts with utility methods:

```java
// Create shortcuts
KeyCombination ctrl_B = KeyCombination.ctrl(KeyEvent.VK_B);
KeyCombination ctrl_shift_Z = KeyCombination.ctrlShift(KeyEvent.VK_Z);

// Display as text
String display = ctrl_B.toDisplayString(); // "Ctrl+B"

// Convert to KeyStroke for Swing
KeyStroke keyStroke = ctrl_B.toKeyStroke();
```

### EditorCommandManager

Centralized command registration and execution:

```java
EditorCommandManager manager = new EditorCommandManager(context);

// Register commands
manager.registerCommand("bold", new BoldCommand());
manager.registerCommand("undo", new UndoCommand(undoManager));

// Execute by ID
manager.executeCommand("bold");

// Get commands by category
Map<CommandCategory, List<RichTextEditorCommand>> byCategory = 
    manager.getCommandsByCategory();
```

## Creating Custom Commands

### Option 1: Extend AbstractEditorCommand

The easiest way to create commands:

```java
public class MyCommand extends AbstractEditorCommand {
    
    public MyCommand() {
        super("My Command", 
              myIcon, 
              CommandCategory.FORMATTING,
              KeyCombination.ctrl(KeyEvent.VK_M));
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        // Your command logic here
        String selectedText = context.getSelectedText();
        context.replaceSelection(selectedText.toUpperCase());
    }
    
    @Override
    public boolean isEnabled(EditorCommandContext context) {
        // Only enable if there's a selection
        return context.hasSelection() && context.isEditable();
    }
}
```

### Option 2: Implement RichTextEditorCommand

For complete control:

```java
public class AdvancedCommand implements RichTextEditorCommand {
    
    @Override
    public String getTitle() { return "Advanced"; }
    
    @Override
    public Icon getIcon() { return loadIcon(); }
    
    @Override
    public KeyCombination[] getKeyCombinations() {
        return new KeyCombination[] {
            KeyCombination.ctrl(KeyEvent.VK_D),
            KeyCombination.alt(KeyEvent.VK_D)
        };
    }
    
    @Override
    public String getTooltip() {
        return "Advanced (Ctrl+D, Alt+D)";
    }
    
    @Override
    public boolean isEnabled(EditorCommandContext context) {
        return true;
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        // Implementation
    }
    
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.EDITING;
    }
}
```

## Built-in Commands

### Formatting Commands
- **BoldCommand**: Toggle bold (Ctrl+B)
- **ItalicCommand**: Toggle italic (Ctrl+I)
- **UnderlineCommand**: Toggle underline (Ctrl+U)
- **FontSizeCommand**: Set font size
- **ForegroundColorCommand**: Set text color
- **ClearFormattingCommand**: Remove all formatting (Ctrl+Shift+X)

### Editing Commands
- **CutCommand**: Cut selection (Ctrl+X)
- **CopyCommand**: Copy selection (Ctrl+C)
- **PasteCommand**: Paste from clipboard (Ctrl+V)
- **SelectAllCommand**: Select all text (Ctrl+A)

### Undo/Redo Commands
- **UndoCommand**: Undo last change (Ctrl+Z)
- **RedoCommand**: Redo last undone change (Ctrl+Y, Ctrl+Shift+Z)

### Insert Commands
- **InsertLinkCommand**: Insert hyperlink (Ctrl+K)
- **InsertImageCommand**: Insert image (Ctrl+Shift+I)

### Navigation Commands
- **FindCommand**: Find text (Ctrl+F)

## Usage Examples

### Basic Setup

```java
// Create editor
JEditorPane editorPane = new JEditorPane();
HTMLEditorKit editorKit = new HTMLEditorKit();
editorPane.setEditorKit(editorKit);

// Create undo manager
UndoManager undoManager = new UndoManager();
editorPane.getDocument().addUndoableEditListener(undoManager);

// Create command context and manager
EditorCommandContext context = new EditorCommandContext(editorPane, parentPanel);
EditorCommandManager commandManager = new EditorCommandManager(context);

// Register commands
commandManager.registerCommand("bold", new BoldCommand());
commandManager.registerCommand("italic", new ItalicCommand());
commandManager.registerCommand("underline", new UnderlineCommand());
commandManager.registerCommand("undo", new UndoCommand(undoManager));
commandManager.registerCommand("redo", new RedoCommand(undoManager));
```

### Building a Toolbar

```java
JToolBar toolbar = new JToolBar();

// Get all commands by category
Map<CommandCategory, List<RichTextEditorCommand>> commandsByCategory = 
    commandManager.getCommandsByCategory();

// Add formatting commands
for (RichTextEditorCommand cmd : commandsByCategory.get(CommandCategory.FORMATTING)) {
    JButton button = new JButton(cmd.getTitle());
    button.setIcon(cmd.getIcon());
    button.setToolTipText(cmd.getTooltip());
    
    button.addActionListener(e -> {
        if (cmd.isEnabled(context)) {
            cmd.execute(context);
        }
    });
    
    toolbar.add(button);
}
```

### Building a Menu

```java
JMenu editMenu = new JMenu("Edit");

for (RichTextEditorCommand cmd : commandManager.getAllCommands()) {
    JMenuItem menuItem = new JMenuItem(cmd.getTitle());
    menuItem.setIcon(cmd.getIcon());
    
    // Set accelerator from first key combination
    KeyCombination[] keys = cmd.getKeyCombinations();
    if (keys.length > 0) {
        menuItem.setAccelerator(keys[0].toKeyStroke());
    }
    
    menuItem.addActionListener(e -> {
        if (cmd.isEnabled(context)) {
            cmd.execute(context);
        }
    });
    
    editMenu.add(menuItem);
}
```

### Programmatic Execution

```java
// Execute by command ID
commandManager.executeCommand("bold");

// Or get command and execute directly
RichTextEditorCommand cmd = commandManager.getCommand("italic");
if (cmd != null && cmd.isEnabled(context)) {
    cmd.execute(context);
}
```

## Command Categories

Commands are organized into categories for better UI organization:

- **FORMATTING**: Text styling (bold, italic, colors, etc.)
- **EDITING**: Text manipulation (cut, copy, paste, etc.)
- **NAVIGATION**: Moving through the document (find, goto, etc.)
- **UNDO_REDO**: History operations
- **INSERT**: Inserting content (links, images, tables, etc.)
- **VIEW**: Display options (zoom, toggle toolbar, etc.)

## Keyboard Shortcuts

Shortcuts are automatically installed when commands are registered. Multiple shortcuts per command are supported:

```java
// Command with multiple shortcuts
public class RedoCommand extends AbstractEditorCommand {
    public RedoCommand(UndoManager undoManager) {
        super("Redo", null, CommandCategory.UNDO_REDO,
              KeyCombination.ctrl(KeyEvent.VK_Y),        // First shortcut
              KeyCombination.ctrlShift(KeyEvent.VK_Z));  // Alternative shortcut
    }
}
```

## Tooltips

Tooltips automatically include keyboard shortcuts:

```java
String tooltip = command.getTooltip();
// Returns: "Bold (Ctrl+B)" or "Redo (Ctrl+Y, Ctrl+Shift+Z)"
```

## Best Practices

1. **Use AbstractEditorCommand**: Extend it for most commands to reduce boilerplate

2. **Check isEnabled()**: Always verify command can execute before running:
   ```java
   if (command.isEnabled(context)) {
       command.execute(context);
   }
   ```

3. **Handle Errors Gracefully**: Catch exceptions in execute() and show user-friendly messages

4. **Keep Commands Focused**: Each command should do one thing well

5. **Use Categories**: Organize commands properly for better UI generation

6. **Provide Icons**: Enhance UX with meaningful icons

7. **Multiple Shortcuts**: Provide alternatives for different user preferences

## Running the Demo

A complete demonstration application is provided:

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.kalynx.swingtheme.themedcomponents.richtexteditor.demo.RichTextEditorCommandDemo"
```

Or run the `RichTextEditorCommandDemo` class from your IDE.

## Architecture Benefits

- **Separation of Concerns**: Commands are independent of UI components
- **Reusability**: Commands can be used in toolbars, menus, and context menus
- **Extensibility**: Easy to add new commands without modifying existing code
- **Testability**: Commands can be tested independently
- **Consistency**: Same command logic across all UI entry points
- **Discoverability**: Centralized command registry makes all actions visible
- **Maintainability**: Changes to command behavior only affect one place

## Advanced Topics

### Dynamic Command State

Commands can be enabled/disabled based on editor state:

```java
@Override
public boolean isEnabled(EditorCommandContext context) {
    // Only enable if there's a selection
    return context.hasSelection() && context.isEditable();
}
```

### Context-Aware Commands

Access full editor state in commands:

```java
@Override
public void execute(EditorCommandContext context) {
    // Access document
    Document doc = context.getDocument();
    
    // Access editor component
    JTextComponent editor = context.getEditor();
    
    // Access parent panel
    JComponent panel = context.getEditorPanel();
}
```

### Command Composition

Create complex commands from simpler ones:

```java
public class MakeHeadingCommand extends AbstractEditorCommand {
    private final BoldCommand bold = new BoldCommand();
    private final FontSizeCommand fontSize = new FontSizeCommand(18);
    
    @Override
    public void execute(EditorCommandContext context) {
        bold.execute(context);
        fontSize.execute(context);
    }
}
```

## License

This framework is part of the Kalynx Swing Custom Theme library.

