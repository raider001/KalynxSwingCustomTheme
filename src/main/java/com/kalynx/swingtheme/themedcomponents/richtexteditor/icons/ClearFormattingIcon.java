package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;

public class ClearFormattingIcon extends EditorIcon {

    public ClearFormattingIcon(int size) {
        super(size);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);
        g2.setStroke(new BasicStroke(2f));

        int padding = size / 4;

        Font font = new Font("Arial", Font.BOLD, (int)(size * 0.5));
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth("T");
        int textHeight = fm.getHeight();

        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - textHeight) / 2 + fm.getAscent();

        g2.drawString("T", textX, textY);

        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(x + padding, y + padding, x + size - padding, y + size - padding);

        g2.dispose();
    }
}

