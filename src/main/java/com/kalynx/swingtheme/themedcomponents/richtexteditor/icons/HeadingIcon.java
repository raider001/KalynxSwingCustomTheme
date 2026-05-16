package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;

public class HeadingIcon extends EditorIcon {

    private final int level;

    public HeadingIcon(int size, int level) {
        super(size);
        this.level = level;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);

        float fontSize = switch (level) {
            case 1 -> size * 0.85f;
            case 2 -> size * 0.75f;
            case 3 -> size * 0.65f;
            case 4 -> size * 0.55f;
            case 5 -> size * 0.50f;
            default -> size * 0.7f;
        };

        Font font = new Font("Arial", Font.BOLD, (int)fontSize);
        g2.setFont(font);

        String text = "H" + level;
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - textHeight) / 2 + fm.getAscent();

        g2.drawString(text, textX, textY);
        g2.dispose();
    }
}

