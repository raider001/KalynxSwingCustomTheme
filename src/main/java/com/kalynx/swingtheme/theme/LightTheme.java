package com.kalynx.swingtheme.theme;

import java.awt.Color;

/**
 * Light Theme - Clean light color scheme for code review
 */
public class LightTheme implements Theme {

    /** Canonical name used for persistence. */
    public static final String THEME_NAME = "Light";

    // Main colors - Warmer tones
    private static final Color BACKGROUND = new Color(252, 250, 245);
    private static final Color FOREGROUND = new Color(35, 30, 25);
    private static final Color SECONDARY_TEXT = new Color(120, 110, 95); // muted warm brown
    private static final Color ACCENT = new Color(200, 115, 50);

    // Component colors - APCA-compliant with warm tones
    private static final Color BUTTON_BG = new Color(242, 238, 230); // Warm light beige
    private static final Color INPUT_BG = new Color(240, 235, 225); // Noticeably darker for clear input field visibility
    private static final Color BORDER = new Color(210, 200, 185); // Warm gray-brown

    // Code review semantic colors
    private static final Color APPROVED = new Color(40, 167, 69);
    private static final Color CHANGES_REQUESTED = new Color(220, 53, 69);

    // Diff colors - warm pastels harmonizing with cream background and warm palette
    private static final Color ADDED = new Color(225, 242, 220);    // Warm sage - yellowy green
    private static final Color REMOVED = new Color(245, 210, 210);  // Warm coral rose
    private static final Color MODIFIED = new Color(255, 230, 180); // Pronounced warm orange/peach

    // Annotation mark colors - saturated versions for visibility on the light scrollbar track
    private static final Color ADDED_ANNOTATION = new Color(34, 139, 34);    // Forest green
    private static final Color REMOVED_ANNOTATION = new Color(195, 40, 40);  // Deep red
    private static final Color MODIFIED_ANNOTATION = new Color(180, 105, 0); // Dark amber

    // Comment annotation mark colors - deeper tones for light scrollbar track
    private static final Color OBSERVATION_ANNOTATION = new Color(21, 101, 192);     // Dark blue
    private static final Color NEEDS_RESOLUTION_ANNOTATION = new Color(230, 81, 0);  // Deep orange
    private static final Color RESOLVED_ANNOTATION = new Color(27, 94, 32);          // Dark green

    @Override
    public Color getBackgroundColor() {
        return BACKGROUND;
    }

    @Override
    public Color getForegroundColor() {
        return FOREGROUND;
    }

    @Override
    public Color getSecondaryTextColor() {
        return SECONDARY_TEXT;
    }

    @Override
    public Color getAccentColor() {
        return ACCENT;
    }

    @Override
    public Color getButtonBackground() {
        return BUTTON_BG;
    }

    @Override
    public Color getButtonForeground() {
        return FOREGROUND;
    }

    @Override
    public Color getInputBackground() {
        return INPUT_BG;
    }

    @Override
    public Color getBorderColor() {
        return BORDER;
    }

    @Override
    public Color getApprovedColor() {
        return APPROVED;
    }

    @Override
    public Color getChangesRequestedColor() {
        return CHANGES_REQUESTED;
    }

    @Override
    public Color getAddedLineColor() {
        return ADDED;
    }

    @Override
    public Color getRemovedLineColor() {
        return REMOVED;
    }

    @Override
    public Color getModifiedLineColor() {
        return MODIFIED;
    }

    @Override
    public Color getAddedAnnotationColor() {
        return ADDED_ANNOTATION;
    }

    @Override
    public Color getRemovedAnnotationColor() {
        return REMOVED_ANNOTATION;
    }

    @Override
    public Color getModifiedAnnotationColor() {
        return MODIFIED_ANNOTATION;
    }

    @Override
    public Color getObservationAnnotationColor() {
        return OBSERVATION_ANNOTATION;
    }

    @Override
    public Color getNeedsResolutionAnnotationColor() {
        return NEEDS_RESOLUTION_ANNOTATION;
    }

    @Override
    public Color getResolvedAnnotationColor() {
        return RESOLVED_ANNOTATION;
    }

    @Override
    public String getName() {
        return THEME_NAME;
    }
}


