# ThemedRichTextEditor - Complete Guide

## Overview

The `ThemedRichTextEditor` is a feature-rich HTML editor component that seamlessly integrates with the ThemedComponents system, supporting both light and dark themes dynamically.

## Features

### ✨ Core Capabilities
- **Full Theme Integration**: Automatically adapts to light and dark themes
- **Command-Based Architecture**: Extensible command system for all editing operations
- **Rich Text Formatting**: Bold, italic, underline, colors, font sizes
- **List Support**: Both ordered (numbered) and unordered (bulleted) lists
- **Indent/Outdent**: Full indentation control with keyboard shortcuts
- **Undo/Redo**: Complete undo/redo support
- **Standard Operations**: Cut, copy, paste, select all
- **Link & Image**: Insert hyperlinks and images
- **Find**: Text search functionality

### 🎨 Theme Support

The editor automatically adapts its appearance based on the current theme:

#### Dark Theme
- Background: `#2A2A30` (dark gray)
- Text: `#E6E6EB` (light gray)
- Accent: `#3A96DD` (blue)
- Code blocks: Darker background with borders

#### Light Theme  
- Background: `#FCF5FA` (cream)
- Text: `#231E19` (dark brown)
- Accent: `#C87332` (orange)
- Code blocks: Lighter background with borders

### 📝 List Functionality

#### Creating Lists

**Unordered Lists (Bullet Points)**
- Keyboard: `Ctrl+Shift+U`
- Toolbar: Click "• List" button
- Converts selected text into bulleted list

**Ordered Lists (Numbered)**
- Keyboard: `Ctrl+Shift+O`
- Toolbar: Click "1. List" button
- Converts selected text into numbered list

#### Indenting Lists

**Indent (Increase Level)**
- Keyboard: `Tab` (when cursor is in list item)
- Toolbar: Click "→ Indent" button
- Moves list item to the right

**Outdent (Decrease Level)**
- Keyboard: `Shift+Tab` (when cursor is in list item)
- Toolbar: Click "← Outdent" button
- Moves list item to the left

#### Nested Lists Example

```html
<ul>
  <li>Parent Item 1</li>
  <li>Parent Item 2
    <ul>
      <li>Child Item 2.1</li>
      <li>Child Item 2.2</li>
    </ul>
  </li>
  <li>Parent Item 3</li>
</ul>
```

## Usage

### Basic Setup

```java
import com.kalynx.swingtheme.themedcomponents.ThemedRichTextEditor;

// Create editor with toolbar
ThemedRichTextEditor editor = new ThemedRichTextEditor();

// Or without toolbar
ThemedRichTextEditor editor = new ThemedRichTextEditor(false);

// Add to your panel
panel.add(editor, BorderLayout.CENTER);
```

### Setting and Getting Content

```java
// Set HTML content
editor.setHtml("<h1>Hello World</h1><p>This is <b>bold</b> text.</p>");

// Get HTML content
String html = editor.getHtml();

// Get plain text
String text = editor.getPlainText();
```

### Executing Commands Programmatically

```java
EditorCommandManager commandManager = editor.getCommandManager();

// Execute a command
commandManager.executeCommand("bold");
commandManager.executeCommand("orderedList");
commandManager.executeCommand("indent");
```

### Theme Switching

```java
ThemeManager themeManager = ThemeManager.getInstance();

// Switch to dark theme
themeManager.setDarkTheme();

// Switch to light theme
themeManager.setLightTheme();

// Editor automatically updates!
```

## Keyboard Shortcuts

### Text Formatting
- `Ctrl+B`: **Bold**
- `Ctrl+I`: *Italic*
- `Ctrl+U`: Underline

### Lists
- `Ctrl+Shift+O`: Create/toggle ordered list
- `Ctrl+Shift+U`: Create/toggle unordered list
- `Tab`: Indent (increase nesting level)
- `Shift+Tab`: Outdent (decrease nesting level)

### Editing
- `Ctrl+Z`: Undo
- `Ctrl+Y` or `Ctrl+Shift+Z`: Redo
- `Ctrl+X`: Cut
- `Ctrl+C`: Copy
- `Ctrl+V`: Paste
- `Ctrl+A`: Select All

## Command System

### Built-in Commands

All commands are registered in the `EditorCommandContext` and managed by the `EditorCommandManager`.

#### Formatting Commands
- `BoldCommand`: Toggle bold formatting
- `ItalicCommand`: Toggle italic formatting
- `UnderlineCommand`: Toggle underline
- `ForegroundColorCommand`: Set text color
- `FontSizeCommand`: Set font size
- `ClearFormattingCommand`: Remove all formatting

#### List Commands
- `OrderedListCommand`: Create/toggle numbered lists
- `UnorderedListCommand`: Create/toggle bullet lists
- `IndentCommand`: Increase indentation
- `OutdentCommand`: Decrease indentation

#### Edit Commands
- `UndoCommand`: Undo last action
- `RedoCommand`: Redo last undone action
- `CutCommand`: Cut selection
- `CopyCommand`: Copy selection
- `PasteCommand`: Paste from clipboard
- `SelectAllCommand`: Select all content

#### Insert Commands
- `InsertLinkCommand`: Insert hyperlink
- `InsertImageCommand`: Insert image

#### Other Commands
- `FindCommand`: Search text

### Creating Custom Commands

```java
public class MyCustomCommand extends AbstractEditorCommand {
    
    public MyCustomCommand() {
        super("My Command", null, CommandCategory.FORMATTING,
              KeyCombination.ctrl(KeyEvent.VK_M));
    }
    
    @Override
    public void execute(EditorCommandContext context) {
        // Your command logic here
        String selectedText = context.getSelectedText();
        context.replaceSelection("<mark>" + selectedText + "</mark>");
    }
}

// Register it
editor.getCommandManager().registerCommand("myCommand", new MyCustomCommand());
```

## Toolbar Customization

The toolbar is automatically created with common commands. You can customize it:

### Accessing Toolbar

```java
// Get the editor pane directly
JEditorPane editorPane = editor.getEditorPane();

// Access command manager
EditorCommandManager commandManager = editor.getCommandManager();
```

### Dropdown Menus

The toolbar includes dropdown menus for:

**Color Menu**
- Red
- Blue
- Green  
- Accent (theme color)

**Font Size Menu**
- 12pt
- 14pt
- 16pt
- 18pt
- 20pt

## Styling

The editor uses CSS stylesheets that automatically adapt to the current theme:

### Body Styles
```css
body {
    color: [theme foreground];
    background-color: [theme input background];
    font-family: 'Segoe UI', Arial, sans-serif;
    font-size: 14px;
    margin: 10px;
}
```

### List Styles
```css
ul, ol {
    margin: 10px 0;
    padding-left: 30px;
}

li {
    margin: 3px 0;
    padding: 2px;
}
```

### Link Styles
```css
a {
    color: [theme accent color];
    text-decoration: underline;
}
```

### Code Block Styles
```css
code {
    background-color: [theme button background];
    border: 1px solid [theme border color];
    padding: 2px 4px;
    font-family: 'Consolas', monospace;
    font-size: 13px;
}
```

## Architecture

### Components

```
ThemedRichTextEditor
├── JEditorPane (HTML editor)
├── HTMLEditorKit (HTML support)
├── UndoManager (undo/redo)
├── EditorCommandManager (command registry)
├── ThemedScrollPane (scrolling)
└── Toolbar (optional)
    ├── Formatting buttons
    ├── List buttons
    ├── Color dropdown
    ├── Size dropdown
    └── Utility buttons
```

### Command Flow

```
User Action → KeyBinding/Button
    ↓
EditorCommandManager.executeCommand(id)
    ↓
Command.isEnabled(context)?
    ↓ (yes)
Command.execute(context)
    ↓
HTMLDocument modified
    ↓
Editor updates visually
```

## Best Practices

### 1. Always Use Commands
Don't directly modify the document. Use commands for proper undo/redo support.

```java
// ❌ Don't do this
editor.getEditorPane().setText("<b>text</b>");

// ✅ Do this
editor.getCommandManager().executeCommand("bold");
```

### 2. Theme-Aware Colors
When registering color commands, use theme colors:

```java
Theme theme = themeManager.getCurrentTheme();
new ForegroundColorCommand("Accent", theme.getAccentColor());
```

### 3. Clean HTML
The editor generates clean, semantic HTML:

```html
<!-- Good: Semantic structure -->
<ul>
  <li>Item 1</li>
  <li>Item 2</li>
</ul>

<!-- Avoid: Inline styling -->
<p style="color: red">Text</p>
```

### 4. Keyboard-First
Design with keyboard shortcuts in mind:
- All major operations have shortcuts
- Tab/Shift+Tab for indentation is intuitive
- Ctrl combinations for formatting

## Example Application

See `RichTextEditorDemo.java` for a complete example featuring:
- Theme switching buttons
- Sample content loading
- Keyboard shortcuts display
- Full toolbar demonstration

## Troubleshooting

### Lists Not Formatting
- Ensure text is selected before creating a list
- Check that the selection includes complete paragraphs

### Indent/Outdent Not Working
- Make sure cursor is within a list item
- Verify no text is selected (these commands work on paragraph level)

### Theme Not Updating
- Editor listens for theme changes automatically
- Ensure you're calling `themeManager.setDarkTheme()` or `setLightTheme()`
- Check that the theme manager instance is the same singleton

### Undo/Redo Issues
- Some operations (like theme changes) don't trigger undo
- HTML paste operations might create undo groups

## Future Enhancements

Potential additions:
- Table support
- Alignment commands (left, center, right, justify)
- Heading styles (H1-H6)
- Block quote formatting
- Horizontal rules
- Text highlight (background color)
- Strikethrough text
- Subscript/superscript
- Print preview
- Export to PDF
- Markdown mode
- Spell check integration

## License

Part of the Kalynx Swing Custom Theme library.

