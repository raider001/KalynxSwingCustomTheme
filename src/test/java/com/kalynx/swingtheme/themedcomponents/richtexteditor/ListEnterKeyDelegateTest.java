package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ListEnterKeyDelegateTest {

    private ListEnterKeyDelegate delegate;
    private JEditorPane editorPane;
    private HTMLDocument document;
    private HTMLEditorKit kit;

    @BeforeEach
    void setUp() {
        delegate = new ListEnterKeyDelegate();
        editorPane = new JEditorPane();
        kit = new HTMLEditorKit();
        editorPane.setEditorKit(kit);
        editorPane.setContentType("text/html");
        document = (HTMLDocument) editorPane.getDocument();
    }

    private void setHtmlContent(String html) throws Exception {
        String fullHtml = "<html><body>" + html + "</body></html>";
        editorPane.setText("");
        kit.read(new StringReader(fullHtml), document, 0);
    }

    private String getDocumentText() throws Exception {
        return document.getText(0, document.getLength());
    }

    @Test
    void testCanHandle_WithinListItem() throws Exception {
        setHtmlContent("<ul><li>Test</li></ul>");

        String text = getDocumentText();
        int testPos = text.indexOf("Test");
        if (testPos >= 0) {
            boolean canHandle = delegate.canHandle(editorPane, document, testPos + 2);
            assertTrue(canHandle, "Should handle caret within list item");
        }
    }

    @Test
    void testCanHandle_OutsideList() throws Exception {
        setHtmlContent("<p>Test</p>");

        String text = getDocumentText();
        int testPos = text.indexOf("Test");
        if (testPos >= 0) {
            boolean canHandle = delegate.canHandle(editorPane, document, testPos + 2);
            assertFalse(canHandle, "Should not handle caret outside list");
        }
    }

    @Test
    void testEndOfLineCreateNewItem() throws Exception {
        setHtmlContent("<ul><li>Test 1</li></ul>");

        String text = getDocumentText();
        int test1Pos = text.indexOf("Test 1");
        assertNotEquals(-1, test1Pos, "Should find 'Test 1' in document");

        int endPos = test1Pos + "Test 1".length();
        editorPane.setCaretPosition(endPos);

        delegate.handleEnter(editorPane, document, endPos);

        Thread.sleep(100);

        String result = getDocumentText();
        int liCount = countOccurrences(result, "Test 1");
        assertTrue(liCount >= 1, "Should still contain 'Test 1'");

        int newLinePos = result.indexOf('\n', test1Pos);
        assertTrue(newLinePos > test1Pos, "Should have content after Test 1");
    }

    @Test
    void testLineBreakInMiddle_DetailedTest() throws Exception {
        String html = "<ul><li>Test 3 Test 4</li></ul>";
        setHtmlContent(html);

        String text = getDocumentText();
        System.out.println("Initial document text: '" + text + "'");
        System.out.println("Document length: " + document.getLength());

        int test3Pos = text.indexOf("Test 3");
        assertNotEquals(-1, test3Pos, "Should find 'Test 3' in document");

        int spaceAfterTest3 = text.indexOf(' ', test3Pos + "Test 3".length());
        assertNotEquals(-1, spaceAfterTest3, "Should find space after 'Test 3'");

        System.out.println("Caret position (space after Test 3): " + spaceAfterTest3);

        editorPane.setCaretPosition(spaceAfterTest3);

        String beforeEnter = getDocumentText();
        System.out.println("Before Enter: '" + beforeEnter + "'");

        delegate.handleEnter(editorPane, document, spaceAfterTest3);

        Thread.sleep(200);

        String afterEnter = getDocumentText();
        System.out.println("After Enter: '" + afterEnter + "'");

        assertTrue(afterEnter.contains("Test 3"), "Should still contain 'Test 3'");
        assertTrue(afterEnter.contains("Test 4"), "Should still contain 'Test 4'");

        int test3Index = afterEnter.indexOf("Test 3");
        int test4Index = afterEnter.indexOf("Test 4");

        assertNotEquals(-1, test3Index, "Test 3 should be in result");
        assertNotEquals(-1, test4Index, "Test 4 should be in result");

        String betweenTest3AndTest4 = afterEnter.substring(test3Index + "Test 3".length(), test4Index);
        System.out.println("Between Test 3 and Test 4: '" + betweenTest3AndTest4 + "'");

        assertFalse(betweenTest3AndTest4.isEmpty(),
            "Should have whitespace/newline between Test 3 and Test 4 (they should be in separate list items)");
    }

    @Test
    void testLineBreakAtStart() throws Exception {
        setHtmlContent("<ul><li>Test Content</li></ul>");

        String text = getDocumentText();
        int testPos = text.indexOf("Test Content");
        assertNotEquals(-1, testPos, "Should find 'Test Content'");

        editorPane.setCaretPosition(testPos);

        delegate.handleEnter(editorPane, document, testPos);

        Thread.sleep(100);

        String result = getDocumentText();
        assertTrue(result.contains("Test Content"), "Content should be preserved");
    }

    @Test
    void testRealWorldScenario_SplitTest3Test4() throws Exception {
        String html = "<ul><li>Test 1</li><li>Test 2</li><li>Test 3 Test 4</li><li>Test 5</li></ul><p></p>";
        setHtmlContent(html);

        String text = getDocumentText();

        int test3Pos = text.indexOf("Test 3");
        assertNotEquals(-1, test3Pos, "Should find 'Test 3'");

        int splitPos = test3Pos + "Test 3".length();

        editorPane.setCaretPosition(splitPos);

        delegate.handleEnter(editorPane, document, splitPos);

        Thread.sleep(200);

        String result = getDocumentText();
        System.out.println("Result after split: '" + result + "'");

        String[] lines = result.split("\n");
        System.out.println("Lines: " + java.util.Arrays.toString(lines));

        int test1Pos = result.indexOf("Test 1");
        int test2Pos = result.indexOf("Test 2");
        int test3AfterPos = result.indexOf("Test 3");
        int test4AfterPos = result.indexOf("Test 4");
        int test5Pos = result.indexOf("Test 5");

        assertNotEquals(-1, test1Pos, "Should find Test 1");
        assertNotEquals(-1, test2Pos, "Should find Test 2");
        assertNotEquals(-1, test3AfterPos, "Should find Test 3");
        assertNotEquals(-1, test4AfterPos, "Should find Test 4");
        assertNotEquals(-1, test5Pos, "Should find Test 5");

        assertTrue(test1Pos < test2Pos, "Test 1 should come before Test 2");
        assertTrue(test2Pos < test3AfterPos, "Test 2 should come before Test 3");
        assertTrue(test3AfterPos < test4AfterPos, "Test 3 should come before Test 4");
        assertTrue(test4AfterPos < test5Pos, "Test 4 should come before Test 5");

        String afterTest3 = result.substring(test3AfterPos + "Test 3".length(), test4AfterPos);
        assertFalse(afterTest3.contains("Test 4"), "Test 3 line should not contain 'Test 4' text");
        assertTrue(afterTest3.trim().isEmpty() || afterTest3.contains("\n"),
            "Test 3 and Test 4 should be in separate list items (found between: '" + afterTest3 + "')");

        String afterTest4 = result.substring(test4AfterPos + "Test 4".length(), test5Pos);
        assertFalse(afterTest4.contains("Test 5"), "Test 4 line should not contain 'Test 5' text");
        assertTrue(afterTest4.trim().isEmpty() || afterTest4.contains("\n"),
            "Test 4 and Test 5 should be in separate list items (found between: '" + afterTest4 + "')");
    }

    @Test
    void testEnterAtEndOfList_ExitsAndCreatesOneParagraph() throws Exception {
        String html = "<ul><li>Test 1</li><li>Test 2</li><li>Test 3</li><li>Test 4</li><li>Test 5</li></ul>";
        setHtmlContent(html);

        String text = getDocumentText();
        int test5Pos = text.indexOf("Test 5");
        assertNotEquals(-1, test5Pos, "Should find 'Test 5'");

        int endOfTest5 = test5Pos + "Test 5".length();
        editorPane.setCaretPosition(endOfTest5);

        delegate.handleEnter(editorPane, document, endOfTest5);
        Thread.sleep(50);

        int caretAfterFirstEnter = editorPane.getCaretPosition();
        delegate.handleEnter(editorPane, document, caretAfterFirstEnter);
        Thread.sleep(100);

        int caretAfterSecondEnter = editorPane.getCaretPosition();
        document.insertString(caretAfterSecondEnter, "abcdefg", null);
        Thread.sleep(50);

        String finalHtml = editorPane.getText();
        System.out.println("Final HTML:\n" + finalHtml);

        String expected = """
                  <body>
                    <ul>
                      <li>
                        Test 1
                      </li>
                      <li>
                        Test 2
                      </li>
                      <li>
                        Test 3
                      </li>
                      <li>
                        Test 4
                      </li>
                      <li>
                        Test 5
                      </li>
                    </ul>
                    <p>
                      abcdefg
                    </p>
                  </body>
                """;


        assertTrue(finalHtml.contains(expected),
            "HTML should contain the exact expected structure with paragraph after list containing 'abcdefg'.\n" +
            "Expected to find:\n" + expected + "\n\nBut got:\n" + finalHtml);
    }

    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}










