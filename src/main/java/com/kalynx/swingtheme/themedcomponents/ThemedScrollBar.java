package com.kalynx.swingtheme.themedcomponents;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * ThemedScrollBar - A JScrollBar that queries theme colors on-demand
 * Provides modern, minimal scrollbar styling with automatic theme integration
 */
public class ThemedScrollBar extends JScrollBar {

    private final ThemeManager themeManager;

    public ThemedScrollBar() {
        super();
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    @SuppressWarnings("MagicConstant")
    public ThemedScrollBar(int orientation) {
        super(orientation);
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    @SuppressWarnings("MagicConstant")
    public ThemedScrollBar(int orientation, int value, int extent, int min, int max) {
        super(orientation, value, extent, min, max);
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    /**
     * Create a scrollbar with type-safe orientation
     */
    @SuppressWarnings("MagicConstant")
    public ThemedScrollBar(ScrollBarOrientation orientation) {
        super(orientation.getValue());
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    /**
     * Create a scrollbar with type-safe orientation and values
     */
    @SuppressWarnings("MagicConstant")
    public ThemedScrollBar(ScrollBarOrientation orientation, int value, int extent, int min, int max) {
        super(orientation.getValue(), value, extent, min, max);
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    /**
     * Initialize with modern UI
     */
    private void initialize() {
        setOpaque(true);
        setPreferredSize(new Dimension(
            themeManager.scale(12),
            themeManager.scale(12)
        ));
        setUI(new ModernScrollBarUI());

        setUnitIncrement(20);
        setBlockIncrement(100);

        getModel().addChangeListener(_ -> repaint());
    }

    @Override
    public void setUI(ScrollBarUI ui) {
        // Only allow our custom UI — reject any L&F attempt to replace it
        if (themeManager == null || ui instanceof ModernScrollBarUI) {
            super.setUI(ui);
        } else {
            super.setUI(new ModernScrollBarUI());
        }
    }

    @Override
    public void updateUI() {
        if (themeManager != null) {
            setUI(new ModernScrollBarUI());
        } else {
            super.updateUI();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Inherit the track colour from the parent scroll pane so the scrollbar
        // automatically matches whatever background the container has been given
        // (e.g. inputBackground in a combo popup vs backgroundColor elsewhere).
        // Fall back to getBackground() when not yet attached to a parent.
        java.awt.Container parent = getParent();
        Color trackBg = (parent != null) ? parent.getBackground() : getBackground();
        g.setColor(trackBg);
        g.fillRect(0, 0, getWidth(), getHeight());

        Theme theme = themeManager.getCurrentTheme();
        Rectangle thumb = calcThumbBounds(getWidth(), getHeight());
        if (thumb == null || thumb.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc     = themeManager.scale(8);
        int padding = themeManager.scale(2);
        g2.setColor(theme.getBorderColor());
        g2.fillRoundRect(
            thumb.x + padding,
            thumb.y + padding,
            thumb.width  - 2 * padding,
            thumb.height - 2 * padding,
            arc, arc);
        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        // Suppress L&F arrow buttons — we paint everything in paintComponent
    }

    private Rectangle calcThumbBounds(int trackW, int trackH) {
        javax.swing.BoundedRangeModel m = getModel();
        int range = m.getMaximum() - m.getMinimum() - m.getExtent();
        if (range <= 0) return null;

        if (getOrientation() == VERTICAL) {
            int thumbH = Math.max(themeManager.scale(20),
                (int)((double) m.getExtent() / (m.getMaximum() - m.getMinimum()) * trackH));
            int thumbY = (int)((double)(m.getValue() - m.getMinimum()) / range * (trackH - thumbH));
            return new Rectangle(0, thumbY, trackW, thumbH);
        } else {
            int thumbW = Math.max(themeManager.scale(20),
                (int)((double) m.getExtent() / (m.getMaximum() - m.getMinimum()) * trackW));
            int thumbX = (int)((double)(m.getValue() - m.getMinimum()) / range * (trackW - thumbW));
            return new Rectangle(thumbX, 0, thumbW, trackH);
        }
    }

    /**
     * Modern, minimal scrollbar UI that queries theme on demand
     */
    /** Forces our themed UI back onto this scrollbar — use after an L&F reset. */
    public void applyTheme() {
        setUI(new ModernScrollBarUI());
    }

    private class ModernScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void installDefaults() {
            super.installDefaults();
            // L&F sets white/system background and a native border in installDefaults —
            // stamp our colours back over them immediately.
            Theme theme = themeManager.getCurrentTheme();
            scrollbar.setBackground(theme.getBackgroundColor());
            scrollbar.setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void configureScrollBarColors() {
            // Query theme on demand
            Theme theme = themeManager.getCurrentTheme();
            thumbColor = theme.getBorderColor();
            thumbDarkShadowColor = theme.getBorderColor();
            thumbHighlightColor = theme.getAccentColor();
            thumbLightShadowColor = theme.getBorderColor();
            trackColor = theme.getBackgroundColor();
            trackHighlightColor = theme.getBackgroundColor();
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        /**
         * Create invisible button to hide arrow buttons (modern look)
         */
        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            // Query theme colors on demand
            Theme theme = themeManager.getCurrentTheme();

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rounded thumb with padding for modern look
            int arc = themeManager.scale(8);
            int padding = themeManager.scale(2);

            Color thumbCol = theme.getBorderColor();
            if (isDragging) {
                thumbCol = theme.getAccentColor();
            } else if (isThumbRollover()) {
                thumbCol = adjustBrightness(theme.getBorderColor());
            }

            g2.setColor(thumbCol);
            g2.fillRoundRect(
                thumbBounds.x + padding,
                thumbBounds.y + padding,
                thumbBounds.width - 2 * padding,
                thumbBounds.height - 2 * padding,
                arc, arc
            );

            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Query theme on demand
            Theme theme = themeManager.getCurrentTheme();
            g.setColor(theme.getBackgroundColor());
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        /**
         * Adjust color brightness for hover effect
         */
        private Color adjustBrightness(Color color) {
            int r = Math.min(255, (int)(color.getRed() * (float) 1.2));
            int g = Math.min(255, (int)(color.getGreen() * (float) 1.2));
            int b = Math.min(255, (int)(color.getBlue() * (float) 1.2));
            return new Color(r, g, b);
        }
    }
}

