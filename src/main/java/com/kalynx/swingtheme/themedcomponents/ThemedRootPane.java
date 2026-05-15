package com.kalynx.swingtheme.themedcomponents;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * A theme-aware {@link JRootPane} that automatically applies theme background colours
 * and provides a {@link Runnable}-based overload of {@link #registerKeyboardAction} to
 * avoid unused {@code ActionEvent} parameters in action-listener lambdas.
 */
public class ThemedRootPane extends JRootPane {

    private final transient ThemeManager themeManager = ThemeManager.getInstance();

    /**
     * Registers a {@link Runnable} as a keyboard action, ignoring the action event.
     *
     * @param action    the action to run when the key stroke is triggered
     * @param keyStroke the key stroke that triggers the action
     * @param condition the focus condition that determines when the action fires
     */
    @SuppressWarnings("MagicConstant")
    public void registerKeyboardAction(Runnable action, KeyStroke keyStroke, FocusCondition condition) {
        registerKeyboardAction(_ -> action.run(), keyStroke, condition.getValue());
    }

    @Override
    public void paint(Graphics g) {
        Theme theme = themeManager.getCurrentTheme();
        Color previous = getBackground();
        super.setBackground(theme.getBackgroundColor());
        super.paint(g);
        super.setBackground(previous);
    }
}

