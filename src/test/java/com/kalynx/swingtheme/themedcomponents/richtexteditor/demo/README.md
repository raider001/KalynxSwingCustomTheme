# HTMLEditorKit Demo

## What This Demonstrates

This demo application shows how `HTMLEditorKit` works in Swing and helps understand:

1. **How HTML is converted to a Document** - See the actual text content stored
2. **Document Structure** - View the element tree and how it's organized
3. **Caret Position Tracking** - See how positions work in the document vs HTML
4. **Formatting Attributes** - Inspect what attributes are applied to text
5. **Why CaretTranslator is Needed** - Understand the HTML vs Document position mismatch

## Running the Demo

```bash
cd KalynxSwingCustomTheme
mvn test-compile
java -cp target/test-classes:target/classes com.kalynx.swingtheme.themedcomponents.richtexteditor.demo.HtmlEditorKitDemo
```

## Features

### Left Panel: HTML Editor
- **Rendered View**: Shows HTML as formatted text (default)
- **Source View**: Toggle to see raw HTML markup
- Edit the content and see updates in real-time

### Right Panel: Document Structure
Shows detailed information about the underlying Document:

1. **Document Info**
   - Type (HTMLDocument)
   - Total length
   - Current caret position

2. **Plain Text Content**
   - Actual text stored in the document
   - Shows `\n` line breaks
   - This is what Document.getText() returns

3. **Document Structure**
   - Tree view of Elements
   - Start/End offsets for each element
   - Content of leaf elements
   - Shows how HTML maps to Document structure

4. **Character Attributes at Caret**
   - Current element under the caret
   - Formatting attributes applied
   - Updates as you move the caret

### Controls

- **Show HTML Source**: Toggle between rendered and source views
- **Refresh**: Force update of the structure view
- **Clear**: Clear all content
- **Load Sample**: Load demonstration HTML

## Key Observations

### 1. HTML Tags Are Not in the Document

```
HTML: "<b>Bold</b> text"
Document: "Bold text"  (no tags!)
```

The tags are stored as *attributes*, not as text.

### 2. Block Elements Create Newlines

```
HTML: "<p>Para 1</p><p>Para 2</p>"
Document: "Para 1\nPara 2"  (newline added!)
```

This is why `</p>`, `</li>`, etc. need special handling in CaretTranslator.

### 3. Caret Position Mismatch

```
HTML Position: "<b>Bold</b> te|xt"  (position ~15)
Document Position: "Bold te|xt"      (position ~8)
```

The HTML string includes tags, the Document doesn't.

### 4. Markup Whitespace is Ignored

```
HTML: "<p>\n  Text\n</p>"  (has newlines/spaces)
Document: "Text"           (just the content)
```

Indentation in HTML markup doesn't appear in the Document.

## Why This Matters for CaretTranslator

The **HTMLCaretTranslator** must:

1. **Skip HTML tags** when counting positions
2. **Count `<br>` and block-end tags** as newline characters
3. **Ignore markup whitespace** (between tags)
4. **Handle HTML entities** as single characters

This demo lets you:
- See the actual Document structure
- Understand caret positions
- Visualize the HTML-to-Document conversion
- Test edge cases interactively

## Usage Tips

1. **Click around** - Watch caret position update
2. **Add formatting** - Bold/italic text (Ctrl+B, Ctrl+I if supported)
3. **Toggle to source** - See the actual HTML generated
4. **Edit and refresh** - Make changes and observe structure updates
5. **Check element tree** - See how HTML elements nest

## What You'll Learn

- Why caret position tracking is complex
- How HTMLEditorKit stores formatting
- The difference between HTML markup and Document content
- Where newlines come from (block elements, not `\n` in HTML)
- How attributes work in styled documents

This understanding is crucial for implementing the rich text editor!

