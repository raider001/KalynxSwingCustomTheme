package com.kalynx.swingtheme.theme.icons;

import com.kalynx.swingtheme.themedcomponents.QuickButton;

import java.awt.*;

/**
 * Draws a padlock icon that visually represents the locked or unlocked state
 * of the window-lock feature.
 * <p>
 * When {@code locked} is {@code true} the shackle is fully closed over the
 * body. When {@code false} the right side of the shackle is raised to show
 * it is open.
 */
public class LockIcon implements QuickButton.IconPainter {

    private final boolean locked;

    /**
     * Creates a lock icon for the given state.
     *
     * @param locked {@code true} renders a closed padlock; {@code false} renders an open one
     */
    public LockIcon(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void paint(Graphics2D g2d, int width, int height, Color foreground) {
        int padV = height / 5;
        int padH = width  / 6;
        int dW   = width  - 2 * padH;
        int dH   = height - 2 * padV;

        g2d.translate(padH, padV);
        g2d.setColor(foreground);

        int cx = dW / 2;

        int bW = Math.max(8, dW * 9 / 20);
        int bH = Math.max(6, dH * 7 / 20);
        int bX = cx - bW / 2;
        int bY = dH / 2 + dH / 10;

        int shR        = bW / 3;
        int shDiameter = shR * 2;
        int shMidY     = bY - shR;
        int arcY       = shMidY - shR;

        float strokeW = Math.max(1.5f, dH / 16f);
        g2d.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2d.drawArc(cx - shR, arcY, shDiameter, shDiameter, 0, 180);
        g2d.drawLine(cx - shR, shMidY, cx - shR, bY);

        if (locked) {
            g2d.drawLine(cx + shR, shMidY, cx + shR, bY);
        }

        g2d.fillRoundRect(bX, bY, bW, bH, 4, 4);

        g2d.translate(-padH, -padV);
    }
}

