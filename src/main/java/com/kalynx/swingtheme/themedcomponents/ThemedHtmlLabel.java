package com.kalynx.swingtheme.themedcomponents;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;
import com.kalynx.swingtheme.utils.HtmlThemeHelper;

/**
 * A label that renders HTML content with theme-aware styling.
 * Colors, fonts, and code/pre styling are injected via {@link HtmlThemeHelper}
 * and refreshed automatically whenever the active theme changes. The label
 * itself stays non-opaque so it blends with its parent container.
 */
public class ThemedHtmlLabel extends ThemedLabel {

    private final ThemeManager themeManager = ThemeManager.getInstance();
    private String rawHtmlContent = "";

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
}
