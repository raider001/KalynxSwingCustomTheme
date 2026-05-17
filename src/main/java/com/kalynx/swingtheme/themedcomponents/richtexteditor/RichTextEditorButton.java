package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class RichTextEditorButton extends JButton {
    @Serial
    private static final long serialVersionUID = 1L;

    private final transient ThemeManager themeManager;
    private final transient BlockedFlashAnimator blockedFlashAnimator;
    private boolean isPressed = false;
    private boolean isActive = false;

    public RichTextEditorButton(Icon icon) {
        super(icon);
        this.themeManager = ThemeManager.getInstance();
        this.blockedFlashAnimator = new BlockedFlashAnimator(this::repaint);
        configureButton();
    }

    @SuppressWarnings("unused")
    public RichTextEditorButton(String text) {
        super(text);
        this.themeManager = ThemeManager.getInstance();
        this.blockedFlashAnimator = new BlockedFlashAnimator(this::repaint);
        configureButton();
    }

    @SuppressWarnings("unused")
    public RichTextEditorButton(String text, Icon icon) {
        super(text, icon);
        this.themeManager = ThemeManager.getInstance();
        this.blockedFlashAnimator = new BlockedFlashAnimator(this::repaint);
        configureButton();
    }

    private void configureButton() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        Dimension size = new Dimension(
            themeManager.scale(32),
            themeManager.scale(32)
        );
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (isEnabled()) {
                    repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });

        themeManager.addThemeChangeListener(this::repaint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Theme theme = themeManager.getCurrentTheme();

        if (isPressed && isEnabled()) {
            g2.setColor(theme.getAccentColor());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                themeManager.scale(6), themeManager.scale(6));
        } else if (isActive && isEnabled()) {
            Color activeColor = new Color(
                theme.getAccentColor().getRed(),
                theme.getAccentColor().getGreen(),
                theme.getAccentColor().getBlue(),
                100
            );
            g2.setColor(activeColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                themeManager.scale(6), themeManager.scale(6));
        } else if (getModel().isRollover() && isEnabled()) {
            Color hoverColor = new Color(
                theme.getAccentColor().getRed(),
                theme.getAccentColor().getGreen(),
                theme.getAccentColor().getBlue(),
                80
            );
            g2.setColor(hoverColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                themeManager.scale(6), themeManager.scale(6));
        } else if (!isEnabled()) {
            g2.setColor(theme.getBackgroundColor());
        } else {
            g2.setColor(theme.getButtonBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                themeManager.scale(6), themeManager.scale(6));
        }

        g2.dispose();
        super.paintComponent(g);

        if (blockedFlashAnimator.getAlpha() > 0f) {
            paintBlockedFlash(g);
        }
    }

    private void paintBlockedFlash(Graphics g) {
        Graphics2D g3 = (Graphics2D) g.create();
        g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color base = themeManager.getCurrentTheme().getErrorColor();
        int alpha = Math.max(0, Math.min(255, Math.round(blockedFlashAnimator.getAlpha() * 255f)));
        g3.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha));
        g3.fillRoundRect(0, 0, getWidth(), getHeight(),
            themeManager.scale(6), themeManager.scale(6));
        g3.dispose();
    }

    /**
     * Plays a brief red fade-in / fade-out flash over the button to signal that
     * the requested command was blocked (e.g. by an exclusion rule). The flash
     * colour is sourced from the active theme's error colour so it stays
     * theme-consistent.
     */
    public void flashBlocked() {
        blockedFlashAnimator.flash();
    }

    public void setActive(boolean active) {
        if (this.isActive != active) {
            this.isActive = active;
            repaint();
        }
    }

    public boolean isActive() {
        return isActive;
    }
}







