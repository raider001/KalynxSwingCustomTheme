package com.kalynx.swingtheme.themedcomponents;

import javax.swing.*;

/**
 * Type-safe wrapper for the focus-condition constants used by
 * {@link JComponent#registerKeyboardAction}.
 *
 * <p>Use this enum instead of the raw {@code JComponent.WHEN_*} int constants
 * when registering keyboard actions via {@link ThemedRootPane}.
 */
public enum FocusCondition {

    /**
     * The action fires when the component itself has keyboard focus.
     *
     * @see JComponent#WHEN_FOCUSED
     */
    WHEN_FOCUSED(JComponent.WHEN_FOCUSED),

    /**
     * The action fires when any component in the same window has keyboard focus.
     *
     * @see JComponent#WHEN_IN_FOCUSED_WINDOW
     */
    WHEN_IN_FOCUSED_WINDOW(JComponent.WHEN_IN_FOCUSED_WINDOW),

    /**
     * The action fires when the component or any of its descendants has keyboard focus.
     *
     * @see JComponent#WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
     */
    WHEN_ANCESTOR_OF_FOCUSED_COMPONENT(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    private final int value;

    FocusCondition(int value) {
        this.value = value;
    }

    /**
     * Returns the underlying {@link JComponent} int constant.
     *
     * @return the raw Swing focus-condition value
     */
    public int getValue() {
        return value;
    }
}

