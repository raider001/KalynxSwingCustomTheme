package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public abstract class EditorIcon implements Icon {
    
    protected final int size;
    protected final ThemeManager themeManager;
    
    public EditorIcon(int size) {
        this.size = size;
        this.themeManager = ThemeManager.getInstance();
    }
    
    @Override
    public int getIconWidth() {
        return size;
    }
    
    @Override
    public int getIconHeight() {
        return size;
    }
    
    protected Color getIconColor() {
        Theme theme = themeManager.getCurrentTheme();
        return theme.getForegroundColor();
    }
    
    protected void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setColor(getIconColor());
    }
}

