package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

public class RedoIcon extends EditorIcon {
    
    public RedoIcon(int size) {
        super(size);
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);
        g2.setStroke(new BasicStroke(2f));
        
        int padding = size / 4;
        int centerX = x + size / 2;
        int centerY = y + size / 2;
        int radius = size / 3;
        
        Arc2D arc = new Arc2D.Float(
            centerX - radius,
            centerY - radius,
            radius * 2,
            radius * 2,
            -45,
            -270,
            Arc2D.OPEN
        );
        g2.draw(arc);
        
        Path2D arrow = new Path2D.Float();
        int arrowX = centerX + radius / 2;
        int arrowY = centerY - radius;
        int arrowSize = size / 5;
        
        arrow.moveTo(arrowX, arrowY);
        arrow.lineTo(arrowX + arrowSize, arrowY + arrowSize / 2);
        arrow.lineTo(arrowX, arrowY + arrowSize);
        arrow.closePath();
        
        g2.fill(arrow);
        g2.dispose();
    }
}

