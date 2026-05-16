package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;

public class ItalicIcon extends EditorIcon {
    
    public ItalicIcon(int size) {
        super(size);
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);
        
        Font font = new Font("Arial", Font.ITALIC | Font.BOLD, (int)(size * 0.8));
        g2.setFont(font);
        
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth("I");
        int textHeight = fm.getHeight();
        
        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - textHeight) / 2 + fm.getAscent();
        
        g2.drawString("I", textX, textY);
        g2.dispose();
    }
}

