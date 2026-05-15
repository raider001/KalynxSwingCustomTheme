package com.kalynx.swingtheme.themedcomponents;

/**
 * Minimal contract for objects that can be displayed as comment annotations
 * on a scroll bar and line-number gutter. Decouples the theming library from
 * application-specific models.
 */
public interface CommentAnnotation {

    /**
     * Returns the 1-based line number of the comment.
     *
     * @return line number
     */
    int getLineNumber();

    /**
     * Returns whether this comment requires resolution.
     *
     * @return true if the comment needs to be resolved
     */
    boolean needsResolution();

    /**
     * Returns whether this comment has been resolved.
     *
     * @return true if resolved
     */
    boolean isResolved();

    /**
     * Returns the author of the comment.
     *
     * @return author name
     */
    String getAuthor();

    /**
     * Returns the comment text.
     *
     * @return comment text
     */
    String getText();

    /**
     * Returns the timestamp of the comment.
     *
     * @return timestamp string
     */
    String getTimestamp();
}


