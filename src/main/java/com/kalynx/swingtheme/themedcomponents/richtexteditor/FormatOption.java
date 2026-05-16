package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.*;

public class FormatOption {
    private final String name;
    private final Icon icon;
    private final String commandId;

    public FormatOption(String name, Icon icon, String commandId) {
        this.name = name;
        this.icon = icon;
        this.commandId = commandId;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getCommandId() {
        return commandId;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FormatOption)) return false;
        FormatOption other = (FormatOption) obj;
        return commandId.equals(other.commandId);
    }

    @Override
    public int hashCode() {
        return commandId.hashCode();
    }
}

