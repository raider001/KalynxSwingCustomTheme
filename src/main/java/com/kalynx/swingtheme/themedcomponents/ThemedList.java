package com.kalynx.swingtheme.themedcomponents;

import java.io.Serial;

import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * A JList that automatically applies and updates theme colors.
 * Colors are applied once per {@link #paint} cycle without triggering
 * additional repaints or allocating objects on every frame.
 */
public class ThemedList<T> extends JList<T> {
    @Serial
    private static final long serialVersionUID = 1L;
    
    protected transient final ThemeManager themeManager;
    
    private transient Font cachedFont;
    private transient int cachedFontSize = -1;

    public ThemedList() {
        super();
        this.themeManager = ThemeManager.getInstance();
        initializeDefaults();
    }

    public ThemedList(T[] items) {
        super(items);
        this.themeManager = ThemeManager.getInstance();
        initializeDefaults();
    }

    public ThemedList(Vector<? extends T> items) {
        super(items);
        this.themeManager = ThemeManager.getInstance();
        initializeDefaults();
    }

    public ThemedList(ListModel<T> model) {
        super(model);
        this.themeManager = ThemeManager.getInstance();
        initializeDefaults();
    }

    private void initializeDefaults() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Don't set fixed cell height - let it size to content
        setFixedCellHeight(-1);
        setFont(resolveFont());
    }

    /**
     * Set a callback to be invoked when an item is selected
     * Handles the valueIsAdjusting check internally
     *
     * @param callback The callback to invoke with the selected item
     */
    public void onItemSelected(Consumer<T> callback) {
        addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                T selected = getSelectedValue();
                if (selected != null && callback != null) {
                    callback.accept(selected);
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Theme theme = themeManager.getCurrentTheme();
        Color previousBackground = getBackground();
        Color previousForeground = getForeground();
        Color previousSelectionBackground = getSelectionBackground();
        Color previousSelectionForeground = getSelectionForeground();
        Font  previousFont       = getFont();

        super.setBackground(theme.getBackgroundColor());
        super.setForeground(theme.getForegroundColor());
        super.setSelectionBackground(theme.getAccentColor());
        super.setSelectionForeground(Color.WHITE);
        super.setFont(resolveFont());

        super.paint(g);

        super.setBackground(previousBackground);
        super.setForeground(previousForeground);
        super.setSelectionBackground(previousSelectionBackground);
        super.setSelectionForeground(previousSelectionForeground);
        super.setFont(previousFont);
    }

    private Font resolveFont() {
        int size = themeManager.scale(12);
        if (cachedFont == null || cachedFontSize != size) {
            cachedFont     = new Font("Segoe UI", Font.PLAIN, size);
            cachedFontSize = size;
        }
        return cachedFont;
    }
}
