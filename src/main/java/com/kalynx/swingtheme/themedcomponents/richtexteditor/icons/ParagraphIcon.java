package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;

public class ParagraphIcon extends EditorIcon {
    
    public ParagraphIcon(int size) {
        super(size);
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);
        
        Font font = new Font("Arial", Font.PLAIN, (int)(size * 0.7));
        g2.setFont(font);
        
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth("P");
        int textHeight = fm.getHeight();
        
        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - textHeight) / 2 + fm.getAscent();
        
        g2.drawString("P", textX, textY);
        g2.dispose();
    }
}

