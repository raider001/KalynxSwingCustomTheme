package com.kalynx.swingtheme.theme;

import java.awt.*;
import java.awt.GraphicsEnvironment;
import java.util.prefs.Preferences;

/**
 * Persists window positions, sizes, and the window-lock state using the
 * Java {@link Preferences} API (backed by the OS user profile / registry).
 * <p>
 * Child window positions are stored as offsets relative to the main frame so
 * they restore correctly regardless of where the main frame is placed between
 * sessions.
 * <p>
 * All methods are static; this class is never instantiated.
 */
class WindowPreferencesManager {

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(WindowPreferencesManager.class);

    private static final String KEY_LOCKED   = "locked";
    private static final String MAIN_PREFIX  = "main.";
    private static final String CHILD_PREFIX = "child.";
    private static final int    UNSET        = Integer.MIN_VALUE;

    private WindowPreferencesManager() {}

    /**
     * Persists the current lock state.
     *
     * @param locked {@code true} if windows are locked
     */
    static void saveLockState(boolean locked) {
        PREFS.putBoolean(KEY_LOCKED, locked);
    }

    /**
     * Loads the persisted lock state, defaulting to {@code false} if not set.
     *
     * @return the persisted lock state
     */
    static boolean loadLockState() {
        return PREFS.getBoolean(KEY_LOCKED, false);
    }

    /**
     * Persists the absolute bounds of the main frame.
     *
     * @param bounds the main frame's current bounds
     */
    static void saveMainFrameBounds(Rectangle bounds) {
        PREFS.putInt(MAIN_PREFIX + "x", bounds.x);
        PREFS.putInt(MAIN_PREFIX + "y", bounds.y);
        PREFS.putInt(MAIN_PREFIX + "w", bounds.width);
        PREFS.putInt(MAIN_PREFIX + "h", bounds.height);
    }

    /**
     * Loads the persisted main frame bounds, or {@code null} if none have been
     * saved or the saved bounds are no longer on any active screen.
     *
     * @return the saved bounds, or {@code null}
     */
    static Rectangle loadMainFrameBounds() {
        int x = PREFS.getInt(MAIN_PREFIX + "x", UNSET);
        if (x == UNSET) {
            return null;
        }
        int y = PREFS.getInt(MAIN_PREFIX + "y", 0);
        int w = PREFS.getInt(MAIN_PREFIX + "w", 0);
        int h = PREFS.getInt(MAIN_PREFIX + "h", 0);
        if (w <= 0 || h <= 0) {
            return null;
        }
        Rectangle bounds = new Rectangle(x, y, w, h);
        return isOnAnyScreen(bounds) ? bounds : null;
    }

    /**
     * Persists a child window's bounds as a position relative to the main frame
     * plus an absolute size.
     *
     * @param windowKey stable identifier for the window (typically its class name)
     * @param relX      {@code child.x - mainFrame.x}
     * @param relY      {@code child.y - mainFrame.y}
     * @param width     child window width
     * @param height    child window height
     */
    static void saveChildBounds(String windowKey, int relX, int relY, int width, int height) {
        String pfx = CHILD_PREFIX + windowKey + ".";
        PREFS.putInt(pfx + "relX",   relX);
        PREFS.putInt(pfx + "relY",   relY);
        PREFS.putInt(pfx + "width",  width);
        PREFS.putInt(pfx + "height", height);
    }

    /**
     * Loads a child window's saved bounds.
     *
     * @param windowKey the key used when the bounds were saved
     * @return an int array {@code [relX, relY, width, height]}, or {@code null}
     *         if no data has been saved for this key
     */
    static int[] loadChildBounds(String windowKey) {
        String pfx = CHILD_PREFIX + windowKey + ".";
        int relX = PREFS.getInt(pfx + "relX", UNSET);
        if (relX == UNSET) {
            return null;
        }
        return new int[]{
            relX,
            PREFS.getInt(pfx + "relY",   0),
            PREFS.getInt(pfx + "width",  0),
            PREFS.getInt(pfx + "height", 0)
        };
    }

    private static boolean isOnAnyScreen(Rectangle bounds) {
        for (GraphicsDevice gd :
                GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            if (gd.getDefaultConfiguration().getBounds().intersects(bounds)) {
                return true;
            }
        }
        return false;
    }
}

