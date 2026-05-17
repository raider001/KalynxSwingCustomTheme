package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;

public class CodeBlockIcon extends EditorIcon {

    public CodeBlockIcon(int size) {
        super(size);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);

        int padding = (int)(size * 0.2);
        int rectX = x + padding;
        int rectY = y + padding;
        int rectWidth = size - (padding * 2);
        int rectHeight = size - (padding * 2);

        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(rectX, rectY, rectWidth, rectHeight);

        Font font = new Font("Consolas", Font.PLAIN, (int)(size * 0.45));
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        String text = "<>";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - textHeight) / 2 + fm.getAscent();

        g2.drawString(text, textX, textY);
        g2.dispose();
    }
}

