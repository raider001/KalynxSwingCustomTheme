package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;
import java.io.IOException;
import java.io.Writer;

public class CompactHTMLWriter extends HTMLWriter {

    public CompactHTMLWriter(Writer w, HTMLDocument doc) {
        super(w, doc);
    }

    public CompactHTMLWriter(Writer w, HTMLDocument doc, int pos, int len) {
        super(w, doc, pos, len);
    }

    @Override
    protected void indent() throws IOException {
    }

    @Override
    protected void incrIndent() {
    }

    @Override
    protected void decrIndent() {
    }

    @Override
    protected void writeLineSeparator() throws IOException {
    }
}

