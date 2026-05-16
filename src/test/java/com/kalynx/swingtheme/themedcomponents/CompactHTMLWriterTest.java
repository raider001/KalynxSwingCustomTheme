package com.kalynx.swingtheme.themedcomponents;

import com.kalynx.swingtheme.themedcomponents.richtexteditor.CompactHTMLWriter;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.StringWriter;

public class CompactHTMLWriterTest {

    public static void main(String[] args) {
        try {
            HTMLEditorKit kit = new HTMLEditorKit();
            HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();

            String sampleHtml =
                "<p>Here's a shopping list:</p>" +
                "<ul>" +
                "<li>Fresh vegetables</li>" +
                "<li>Organic fruits</li>" +
                "<li>Whole grain bread</li>" +
                "<li>Dairy products</li>" +
                "</ul>";

            kit.read(new java.io.StringReader(sampleHtml), doc, 0);

            StringWriter writer = new StringWriter();
            CompactHTMLWriter htmlWriter = new CompactHTMLWriter(writer, doc);
            htmlWriter.write();

            String output = writer.toString();
            System.out.println("Compact HTML Output:");
            System.out.println(output);
            System.out.println();
            System.out.println("Verification:");
            System.out.println("- Contains 'Fresh vegetables': " + output.contains("Fresh vegetables"));
            System.out.println("- Contains 'Organic fruits': " + output.contains("Organic fruits"));
            System.out.println("- Contains excessive newlines: " + output.contains("\n  \n"));
            System.out.println("- Line count: " + output.split("\n").length);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

