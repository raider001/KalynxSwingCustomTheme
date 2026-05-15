package com.kalynx.swingtheme.theme;

import java.awt.Color;

/**
 * Theme interface - defines colors for the application
 */
public interface Theme {
    
    // Main colors
    Color getBackgroundColor();
    Color getForegroundColor();
    Color getSecondaryTextColor();  // muted text for subtitles / labels
    Color getAccentColor();

    // Component colors
    Color getButtonBackground();
    Color getButtonForeground();
    Color getInputBackground();
    Color getBorderColor();

    // Semantic colors for code review
    Color getApprovedColor();
    Color getChangesRequestedColor();
    
    // Diff colors (used for line background highlights in the diff view)
    Color getAddedLineColor();
    Color getRemovedLineColor();
    Color getModifiedLineColor();

    // Annotation mark colors (used for the scrollbar overview ruler — must be
    // clearly visible against the scrollbar track, so they are intentionally
    // more saturated than the corresponding diff line background colors)
    default Color getAddedAnnotationColor() { return getAddedLineColor(); }
    default Color getRemovedAnnotationColor() { return getRemovedLineColor(); }
    default Color getModifiedAnnotationColor() { return getModifiedLineColor(); }

    // Comment annotation mark colors (scrollbar overview ruler)
    default Color getObservationAnnotationColor() { return new java.awt.Color(33, 150, 243); }
    default Color getNeedsResolutionAnnotationColor() { return new java.awt.Color(255, 152, 0); }
    default Color getResolvedAnnotationColor() { return new java.awt.Color(76, 175, 80); }

    // Get a theme name
    String getName();
}
