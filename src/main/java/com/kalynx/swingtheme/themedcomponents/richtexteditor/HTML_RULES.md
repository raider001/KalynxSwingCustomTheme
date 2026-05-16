# HTML Structure Rules for Rich Text Editor

## Core Principle
All block-level elements must exist at the document root level. No nesting of block elements within other block elements.

**Exception**: Lists can be nested within other lists for hierarchical content.

## Block Elements
The following are treated as top-level block elements:
- `<p>` - Paragraph
- `<h1>` through `<h5>` - Headings
- `<ul>` - Unordered lists
- `<ol>` - Ordered lists

## Rules

### 1. Root Level Only (Block Elements)
**Block elements (paragraphs, headings) must be direct children of the document body.**
- ✅ Valid: `<body><h1>Title</h1><p>Text</p><ul><li>Item</li></ul></body>`
- ❌ Invalid: `<p><ul><li>Item</li></ul></p>` (list inside paragraph)
- ❌ Invalid: `<h1><p>Text</p></h1>` (paragraph inside heading)

### 2. Nested Lists (Exception to Rule 1)
**Lists CAN be nested within other lists for hierarchical content.**
- ✅ Valid nested list:
```html
<ul>
  <li>Item 1</li>
  <li>Item 2
    <ul>
      <li>Nested 2.1</li>
      <li>Nested 2.2</li>
    </ul>
  </li>
  <li>Item 3</li>
</ul>
```
- ✅ Valid mixed list types:
```html
<ol>
  <li>Numbered item
    <ul>
      <li>Bullet A</li>
      <li>Bullet B</li>
    </ul>
  </li>
</ol>
```

### 3. List Items
**List items (`<li>`) may only exist within list elements.**
- ✅ Valid: `<ul><li>Item</li></ul>`
- ❌ Invalid: `<p><li>Item</li></p>`

### 4. Inline Elements
**Inline formatting elements can exist within block elements:**
- `<b>`, `<strong>` - Bold
- `<i>`, `<em>` - Italic
- `<u>` - Underline

Example: `<h1>Title with <b>bold</b> text</h1>` ✅

### 5. Format Switching
**When changing format, the current block element is replaced:**
- Changing a paragraph to H1: `<p>Text</p>` → `<h1>Text</h1>`
- Changing H1 to list: `<h1>Text</h1>` → `<ul><li>Text</li></ul>`

### 6. List Nesting Operations
**Use indent/outdent to create nested lists:**
- **Indent**: Increases list depth (creates nested list)
- **Outdent**: Decreases list depth (moves to parent level)

### 7. Document Structure
**Valid document structure with nested lists:**
```html
<html>
  <body>
    <h1>Title</h1>
    <p>Paragraph</p>
    <ul>
      <li>Item 1</li>
      <li>Item 2
        <ul>
          <li>Nested 2.1</li>
          <li>Nested 2.2</li>
        </ul>
      </li>
    </ul>
    <ol>
      <li>First
        <ol>
          <li>Sub A</li>
        </ol>
      </li>
    </ol>
  </body>
</html>
```

## Implementation Notes

### Why These Rules?
Java's HTMLEditorKit (HTML 3.2) has quirks with nested block elements:
- `insertHTML()` can unexpectedly restructure documents
- Element offsets become invalid after complex edits
- Paragraph elements wrap content in unexpected ways
- **Exception**: Nested lists work reliably (HTML 3.2 spec compliant)

### Enforcement
- All format commands use `popDepth=1` to ensure root-level insertion
- Indent/Outdent commands handle list nesting directly
- The editor validates structure before operations

### Future Considerations
- Nested list depth could be limited (e.g., max 5 levels)
- Block quotes could be added as root-level elements
- Tables would also be root-level elements
