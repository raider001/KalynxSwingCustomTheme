package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import java.awt.*;
import java.awt.geom.Path2D;

public class IndentIcon extends EditorIcon {
    
    public IndentIcon(int size) {
        super(size);
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        setupGraphics(g2);
        g2.setStroke(new BasicStroke(2f));
        
        int padding = size / 4;
        int lineStart = x + padding;
        int lineEnd = x + size - padding;
        
        g2.drawLine(lineStart, y + padding, lineEnd, y + padding);
        g2.drawLine(lineStart, y + size - padding, lineEnd, y + size - padding);
        
        Path2D arrow = new Path2D.Float();
        int arrowX = x + padding / 2;
        int arrowY = y + size / 2;
        int arrowSize = size / 4;
        
        arrow.moveTo(arrowX, arrowY);
        arrow.lineTo(arrowX + arrowSize, arrowY - arrowSize / 2);
        arrow.lineTo(arrowX + arrowSize, arrowY + arrowSize / 2);
        arrow.closePath();
        
        g2.fill(arrow);
        g2.dispose();
    }
}

