package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class KeyCombination {
    
    private final int keyCode;
    private final int modifiers;
    
    public KeyCombination(int keyCode, int modifiers) {
        this.keyCode = keyCode;
        this.modifiers = modifiers;
    }
    
    public int getKeyCode() {
        return keyCode;
    }
    
    public int getModifiers() {
        return modifiers;
    }
    
    public KeyStroke toKeyStroke() {
        return KeyStroke.getKeyStroke(keyCode, modifiers);
    }
    
    public String toDisplayString() {
        List<String> parts = new ArrayList<>();
        
        if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
            parts.add("Ctrl");
        }
        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
            parts.add("Shift");
        }
        if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
            parts.add("Alt");
        }
        if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
            parts.add("Meta");
        }
        
        parts.add(KeyEvent.getKeyText(keyCode));
        
        return String.join("+", parts);
    }
    
    public static KeyCombination ctrl(int keyCode) {
        return new KeyCombination(keyCode, InputEvent.CTRL_DOWN_MASK);
    }
    
    public static KeyCombination ctrlShift(int keyCode) {
        return new KeyCombination(keyCode, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
    }
    
    public static KeyCombination alt(int keyCode) {
        return new KeyCombination(keyCode, InputEvent.ALT_DOWN_MASK);
    }
    
    public static KeyCombination simple(int keyCode) {
        return new KeyCombination(keyCode, 0);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof KeyCombination)) return false;
        KeyCombination other = (KeyCombination) obj;
        return keyCode == other.keyCode && modifiers == other.modifiers;
    }
    
    @Override
    public int hashCode() {
        return 31 * keyCode + modifiers;
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}

