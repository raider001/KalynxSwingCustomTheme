package com.kalynx.swingtheme.themedcomponents;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;
import com.kalynx.swingtheme.utils.HtmlThemeHelper;

import java.awt.Container;
import java.awt.Dimension;

/**
 * A label that renders HTML content with theme-aware styling.
 * Colors, fonts, and code/pre styling are injected via {@link HtmlThemeHelper}
 * and refreshed automatically whenever the active theme changes. The label
 * itself stays non-opaque so it blends with its parent container.
 * <p>
 * The preferred size calculation constrains the label to its parent's width so
 * that HTML content reflows and wraps correctly when the container is resized.
 */
public class ThemedHtmlLabel extends ThemedLabel {

    private final ThemeManager themeManager = ThemeManager.getInstance();
    private String rawHtmlContent = "";
    private boolean applyingWidth = false;

    /**
     * Creates an empty themed HTML label.
     */
    public ThemedHtmlLabel() {
        this("");
    }

    /**
     * Creates a themed HTML label with the given raw HTML body content.
     *
     * @param html raw HTML body content; may be {@code null}
     */
    public ThemedHtmlLabel(String html) {
        super();
        setOpaque(false);
        setHtmlContent(html);
        themeManager.addThemeChangeListener(this::updateStyledHtml);
    }

    /**
     * Sets the raw HTML body content. The content is wrapped and themed
     * automatically; callers should not embed theme-specific styles.
     *
     * @param html raw HTML content; {@code null} clears the label
     */
    public void setHtmlContent(String html) {
        this.rawHtmlContent = html != null ? html : "";
        updateStyledHtml();
    }

    private void updateStyledHtml() {
        if (rawHtmlContent.isEmpty()) {
            setText("");
            return;
        }
        Theme theme = themeManager.getCurrentTheme();
        setText(HtmlThemeHelper.applyThemeStyles(rawHtmlContent, theme));
    }

    /**
     * Overrides preferred size so that when a parent container constrains the
     * available width, the HTML renderer reflows content and wraps text at that
     * width rather than reporting the full unwrapped content width.
     *
     * @return the preferred size, clamped to the parent's current width when available
     */
    @Override
    public Dimension getPreferredSize() {
        if (applyingWidth) {
            return super.getPreferredSize();
        }
        Dimension size = super.getPreferredSize();
        Container parent = getParent();
        if (parent != null && parent.getWidth() > 0) {
            int maxWidth = parent.getWidth();
            if (size.width > maxWidth) {
                applyingWidth = true;
                try {
                    setSize(maxWidth, Short.MAX_VALUE);
                    size = super.getPreferredSize();
                    size.width = maxWidth;
                } finally {
                    applyingWidth = false;
                }
            }
        }
        return size;
    }
}
