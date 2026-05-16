package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;

public class OrderedListIcon extends EditorIcon {
    
    public OrderedListIcon(int size) {
        super(size);
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);
        g2.setStroke(new BasicStroke(1.5f));
        
        int padding = size / 6;
        int lineHeight = size / 4;
        Font font = new Font("Arial", Font.PLAIN, size / 3);
        g2.setFont(font);
        
        for (int i = 0; i < 3; i++) {
            int ly = y + padding + (i * lineHeight);
            
            g2.drawString(String.valueOf(i + 1) + ".", x + padding / 2, ly + lineHeight / 2);
            
            int lineX = x + padding * 2;
            int lineWidth = size - padding * 3;
            g2.drawLine(lineX, ly, lineX + lineWidth, ly);
        }
        
        g2.dispose();
    }
}

