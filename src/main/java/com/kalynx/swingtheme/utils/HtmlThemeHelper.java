package com.kalynx.swingtheme.utils;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for injecting theme-aware styling into HTML content used by
 * Swing components such as {@code ThemedHtmlLabel} and the rich text editor.
 */
public class HtmlThemeHelper {

    private static final Pattern BODY_PATTERN =
        Pattern.compile("(?is)<body[^>]*>(.*?)</body>");
    private static final Pattern HTML_WRAPPER_PATTERN =
        Pattern.compile("(?is)^\\s*<html[^>]*>(.*?)</html>\\s*$");
    private static final Pattern STYLE_BLOCK_PATTERN =
        Pattern.compile("(?is)<style[^>]*>.*?</style>");
    private static final Pattern HEAD_BLOCK_PATTERN =
        Pattern.compile("(?is)<head[^>]*>.*?</head>");

    /**
     * Applies theme-aware styling to the given HTML content using the
     * default base font size.
     *
     * @param content raw HTML content
     * @param theme   active theme
     * @return themed HTML string ready to be rendered
     */
    public static String applyThemeStyles(String content, Theme theme) {
        return applyThemeStyles(content, theme, ThemeManager.getInstance().getBaseFontSize());
    }

    /**
     * Applies theme-aware styling to the given HTML content using the
     * supplied base font size.
     *
     * @param content  raw HTML content
     * @param theme    active theme
     * @param fontSize base font size in points
     * @return themed HTML string ready to be rendered
     */
    public static String applyThemeStyles(String content, Theme theme, int fontSize) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        String body = extractBodyContent(content);
        String codeStyles = generateCodeStyleBlock(theme, fontSize);
        String bodyInlineStyle = generateBodyInlineStyle(theme, fontSize);

        return "<html><head>" + codeStyles + "</head>"
            + "<body style=\"" + bodyInlineStyle + "\">" + body + "</body></html>";
    }

    /**
     * Generates a complete {@code <style>} block (legacy helper) targeting
     * body and code/pre elements. Prefer {@link #applyThemeStyles} for new code.
     *
     * @param theme    active theme
     * @param fontSize base font size in points
     * @return a {@code <style>...</style>} string
     */
    @SuppressWarnings("unused")
    public static String generateStyleTag(Theme theme, int fontSize) {
        return "<style>"
            + "body { " + generateBodyInlineStyle(theme, fontSize) + " } "
            + extractCodeRules(theme, fontSize)
            + "</style>";
    }

    /**
     * Adds editor-friendly theme rules to the supplied {@link javax.swing.text.html.StyleSheet}.
     * The body rule sets text color and font but intentionally omits {@code background-color}
     * so the host {@code JEditorPane}'s {@code setBackground} can paint the full pane through
     * the transparent body box.
     *
     * @param styleSheet   stylesheet to populate
     * @param theme        active theme
     * @param baseFontSize base font size in points
     * @param fontFamily   primary font family
     */
    public static void applyEditorStyleRules(javax.swing.text.html.StyleSheet styleSheet,
                                             Theme theme, int baseFontSize, String fontFamily) {
        styleSheet.addRule("body { " +
            "color: " + toHex(theme.getForegroundColor()) + "; " +
            "font-family: '" + fontFamily + "', Arial, sans-serif; " +
            "font-size: " + baseFontSize + "pt; " +
            "margin: 10px; " +
            "}");

        styleSheet.addRule("p { margin: 8px 0; font-size: " + baseFontSize + "pt; }");
        styleSheet.addRule("h1 { font-size: " + (baseFontSize * 2) + "pt; font-weight: bold; margin: 16px 0; }");
        styleSheet.addRule("h2 { font-size: " + (int)(baseFontSize * 1.67) + "pt; font-weight: bold; margin: 14px 0; }");
        styleSheet.addRule("h3 { font-size: " + (int)(baseFontSize * 1.33) + "pt; font-weight: bold; margin: 12px 0; }");
        styleSheet.addRule("h4 { font-size: " + (int)(baseFontSize * 1.17) + "pt; font-weight: bold; margin: 10px 0; }");
        styleSheet.addRule("h5 { font-size: " + baseFontSize + "pt; font-weight: bold; margin: 8px 0; }");
        styleSheet.addRule("ul, ol { margin: 10px 0; padding-left: 30px; }");
        styleSheet.addRule("li { margin: 3px 0; padding: 2px; }");
        styleSheet.addRule("a { color: " + toHex(theme.getAccentColor()) + "; text-decoration: underline; }");
        styleSheet.addRule("strong, b { font-weight: bold; }");
        styleSheet.addRule("em, i { font-style: italic; }");
        styleSheet.addRule("u { text-decoration: underline; }");

        styleSheet.addRule("code { " +
            "background-color: " + toHex(theme.getButtonBackground()) + "; " +
            "border: 1px solid " + toHex(theme.getBorderColor()) + "; " +
            "padding: 2px 4px; " +
            "font-family: 'Consolas', monospace; " +
            "font-size: " + Math.max(baseFontSize - 1, 8) + "pt; " +
            "}");

        styleSheet.addRule("pre { " +
            "background-color: " + toHex(theme.getButtonBackground()) + "; " +
            "border: 1px solid " + toHex(theme.getBorderColor()) + "; " +
            "padding: 10px; " +
            "font-family: 'Consolas', monospace; " +
            "overflow-x: auto; " +
            "}");
    }

    /**
     * Converts a {@link Color} into a {@code #rrggbb} hex string.
     *
     * @param color color to convert
     * @return hex string representation
     */
    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static String extractBodyContent(String content) {
        Matcher bodyMatcher = BODY_PATTERN.matcher(content);
        if (bodyMatcher.find()) {
            return bodyMatcher.group(1);
        }

        Matcher htmlMatcher = HTML_WRAPPER_PATTERN.matcher(content);
        String inner = htmlMatcher.matches() ? htmlMatcher.group(1) : content;

        inner = HEAD_BLOCK_PATTERN.matcher(inner).replaceAll("");
        inner = STYLE_BLOCK_PATTERN.matcher(inner).replaceAll("");
        return inner;
    }

    private static String generateBodyInlineStyle(Theme theme, int fontSize) {
        String fontFamily = ThemeManager.getInstance().getBaseFontFamily();
        return String.format(
            "font-family: '%s', Arial, sans-serif; font-size: %dpt; color: %s; margin: 0; padding: 0;",
            fontFamily, fontSize, toHex(theme.getForegroundColor())
        );
    }

    private static String generateCodeStyleBlock(Theme theme, int fontSize) {
        return "<style>" + extractCodeRules(theme, fontSize) + "</style>";
    }

    private static String extractCodeRules(Theme theme, int fontSize) {
        String fgHex = toHex(theme.getForegroundColor());
        String codeBgHex = toHex(theme.getInputBackground());
        int codeFontSize = Math.max(fontSize - 1, 8);
        return String.format(
            "code, pre { background-color: %s; color: %s; padding: 2px 4px; "
                + "font-family: Consolas, monospace; font-size: %dpt; } "
                + "pre { padding: 4px; margin: 4px 0; } ",
            codeBgHex, fgHex, codeFontSize
        );
    }
}
