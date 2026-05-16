package com.kalynx.swingtheme.themedcomponents.richtexteditor.icons;

import javax.swing.*;

public class EditorIconFactory {

    private static final int DEFAULT_SIZE = 20;

    public static Icon getBoldIcon() {
        return getBoldIcon(DEFAULT_SIZE);
    }

    public static Icon getBoldIcon(int size) {
        return new BoldIcon(size);
    }

    public static Icon getItalicIcon() {
        return getItalicIcon(DEFAULT_SIZE);
    }

    public static Icon getItalicIcon(int size) {
        return new ItalicIcon(size);
    }

    public static Icon getUnderlineIcon() {
        return getUnderlineIcon(DEFAULT_SIZE);
    }

    public static Icon getUnderlineIcon(int size) {
        return new UnderlineIcon(size);
    }

    public static Icon getOrderedListIcon() {
        return getOrderedListIcon(DEFAULT_SIZE);
    }

    public static Icon getOrderedListIcon(int size) {
        return new OrderedListIcon(size);
    }

    public static Icon getUnorderedListIcon() {
        return getUnorderedListIcon(DEFAULT_SIZE);
    }

    public static Icon getUnorderedListIcon(int size) {
        return new UnorderedListIcon(size);
    }

    public static Icon getIndentIcon() {
        return getIndentIcon(DEFAULT_SIZE);
    }

    public static Icon getIndentIcon(int size) {
        return new IndentIcon(size);
    }

    public static Icon getOutdentIcon() {
        return getOutdentIcon(DEFAULT_SIZE);
    }

    public static Icon getOutdentIcon(int size) {
        return new OutdentIcon(size);
    }

    public static Icon getUndoIcon() {
        return getUndoIcon(DEFAULT_SIZE);
    }

    public static Icon getUndoIcon(int size) {
        return new UndoIcon(size);
    }

    public static Icon getRedoIcon() {
        return getRedoIcon(DEFAULT_SIZE);
    }

    public static Icon getRedoIcon(int size) {
        return new RedoIcon(size);
    }

    public static Icon getClearFormattingIcon() {
        return getClearFormattingIcon(DEFAULT_SIZE);
    }

    public static Icon getClearFormattingIcon(int size) {
        return new ClearFormattingIcon(size);
    }
}

