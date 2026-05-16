package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;

public class NormalIcon extends EditorIcon {
    
    public NormalIcon(int size) {
        super(size);
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);
        
        int padding = size / 4;
        int lineY = y + padding;
        int lineSpacing = (int)(size * 0.25);
        
        g2.setStroke(new BasicStroke(1.5f));
        
        for (int i = 0; i < 3; i++) {
            g2.drawLine(x + padding, lineY, x + size - padding, lineY);
            lineY += lineSpacing;
        }
        
        g2.dispose();
    }
}

