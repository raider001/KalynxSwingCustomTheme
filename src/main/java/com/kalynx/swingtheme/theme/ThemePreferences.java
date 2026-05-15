package com.kalynx.swingtheme.theme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Persists and restores the user's chosen theme across application restarts.
 * Backed by {@link Preferences#userNodeForPackage} so each OS user has their own preference.
 */
public class ThemePreferences {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemePreferences.class);
    private static final String KEY_THEME_NAME = "theme.name";

    private final Preferences prefs = Preferences.userNodeForPackage(ThemePreferences.class);

    /**
     * Persists the given theme name.
     *
     * @param themeName the value returned by {@link Theme#getName()}
     */
    public void save(String themeName) {
        prefs.put(KEY_THEME_NAME, themeName);
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            LOGGER.warn("Failed to flush theme preference to backing store", e);
        }
    }

    /**
     * Returns the previously persisted theme name, or {@code defaultName} if none was saved.
     *
     * @param defaultName fallback value when no preference exists
     * @return the stored theme name
     */
    public String load(String defaultName) {
        return prefs.get(KEY_THEME_NAME, defaultName);
    }
}

