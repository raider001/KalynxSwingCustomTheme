package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;

public class UnorderedListIcon extends EditorIcon {
    
    public UnorderedListIcon(int size) {
        super(size);
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);
        g2.setStroke(new BasicStroke(1.5f));
        
        int padding = size / 6;
        int lineHeight = size / 4;
        int bulletSize = size / 8;
        
        for (int i = 0; i < 3; i++) {
            int ly = y + padding + (i * lineHeight);
            
            g2.fillOval(x + padding / 2, ly - bulletSize / 2, bulletSize, bulletSize);
            
            int lineX = x + padding * 2;
            int lineWidth = size - padding * 3;
            g2.drawLine(lineX, ly, lineX + lineWidth, ly);
        }
        
        g2.dispose();
    }
}

