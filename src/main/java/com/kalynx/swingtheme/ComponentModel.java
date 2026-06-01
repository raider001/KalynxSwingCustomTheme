package com.kalynx.swingtheme;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ComponentModel<T> {

    private boolean notifying = false;
    private T value;
    private final List<Consumer<T>> onChange = new ArrayList<>();

    public T getValue() {
        return value;
    }

    public synchronized void setValue(T value) {
        if (notifying || isEqual(this.value, value)) {
            return;
        }

        this.value = value;
        notifyChange();
    }

    /**
     * Sets the value and always notifies listeners, bypassing the equality check.
     * Use this when the value may be structurally equal but listeners still need to react.
     *
     * @param value the new value to set
     */
    public synchronized void forceSetValue(T value) {
        if (notifying) {
            return;
        }
        this.value = value;
        notifyChange();
    }

    public synchronized void addChangeListener(Consumer<T> listener) {
        onChange.add(listener);
        listener.accept(value);
    }

    public synchronized void removeChangeListener(Consumer<T> listener) {
        onChange.remove(listener);
    }

    private void notifyChange() {
        if (notifying) {
            return;
        }

        try {
            notifying = true;
            for (Consumer<T> listener : onChange) {
                listener.accept(value);
            }
        } finally {
            notifying = false;
        }
    }

    private boolean isEqual(T oldValue, T newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }
        if (oldValue == null || newValue == null) {
            return false;
        }
        return oldValue.equals(newValue);
    }
}
