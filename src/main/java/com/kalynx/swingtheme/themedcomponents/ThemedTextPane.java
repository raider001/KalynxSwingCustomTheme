package com.kalynx.swingtheme.themedcomponents;

import java.io.Serial;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * ThemedTextPane - A theme-aware styled text pane for code/diff viewing
 */
public class ThemedTextPane extends JTextPane {
    @Serial
    private static final long serialVersionUID = 1L;

    private transient final ThemeManager themeManager;

    public ThemedTextPane() {
        super();
        this.themeManager = ThemeManager.getInstance();
        setEditable(false);
        setFont(new Font("Consolas", Font.PLAIN, themeManager.scale(12)));
        setMargin(new Insets(themeManager.scale(5), themeManager.scale(5), themeManager.scale(5), themeManager.scale(5)));
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
     * Returns a preferred size that is resilient against re-entrant Swing view-tree
     * initialization. The default {@link JTextPane#getPreferredSize()} can throw a
     * {@link NullPointerException} from {@code FlowView$FlowStrategy.layoutRow} when
     * called during the initial layout pass before the view tree is ready. In that
     * case we fall back to an estimate based on the current text and font metrics.
     *
     * @return the preferred size, never throws
     */
    @Override
    public Dimension getPreferredSize() {
        try {
            return super.getPreferredSize();
        } catch (Exception ignored) {
            return estimatePreferredSize();
        }
    }

    private Dimension estimatePreferredSize() {
        Font font = getFont();
        FontMetrics fm = font != null ? getFontMetrics(font) : null;
        int lineHeight = fm != null ? fm.getHeight() : themeManager.scale(14);
        String text = getText();
        int lineCount = text == null || text.isEmpty() ? 1 : text.split("\n", -1).length;
        Insets insets = getInsets();
        int width = getWidth() > 0 ? getWidth() : 200;
        int height = lineCount * lineHeight + (insets != null ? insets.top + insets.bottom : 0);
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (themeManager != null) {
            Theme theme = themeManager.getCurrentTheme();
            setBackground(theme.getBackgroundColor());
            setForeground(theme.getForegroundColor());
            setCaretColor(theme.getAccentColor());
            setSelectionColor(theme.getAccentColor());
            setSelectedTextColor(Color.WHITE);
        }
        super.paintComponent(g);
    }
}



