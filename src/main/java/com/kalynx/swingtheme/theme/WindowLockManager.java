package com.kalynx.swingtheme.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton manager for two window-management features.
 * <p>
 * <b>Window Lock</b>: When enabled, all registered child windows follow the
 * main frame whenever it is dragged via its title bar. Children are repositioned
 * in the same EDT call, before the main frame itself moves, so the OS compositor
 * sees all windows at their new positions simultaneously.
 * <p>
 * <b>Auto-Snap</b>: {@link #applySnap} snaps a dragged window to the nearest
 * edge or aligned edge of any other registered window within
 * {@value #SNAP_DISTANCE} pixels. Snapping is always active.
 * <p>
 * <b>Resize Snap</b>: When a child window is resized, the moved edge snaps to
 * the nearest vertical or horizontal edge of any other registered window.
 * <p>
 * Usage:
 * <pre>
 *   WindowLockManager.getInstance().registerMainFrame(frame);
 *   WindowLockManager.getInstance().registerChildWindow(dialog);
 * </pre>
 */
public class WindowLockManager {

    private static final WindowLockManager INSTANCE = new WindowLockManager();
    private static final int SNAP_DISTANCE = 12;

    private JFrame mainFrame;
    private boolean locked = false;

    private final Set<Window> children = new LinkedHashSet<>();
    private final List<Runnable> lockStateListeners = new CopyOnWriteArrayList<>();
    private final Map<Window, Rectangle> childLastBounds = Collections.synchronizedMap(new WeakHashMap<>());
    private final Set<Window> snappingInProgress = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private WindowLockManager() {}

    /**
     * Returns the singleton instance.
     *
     * @return the singleton {@code WindowLockManager}
     */
    public static WindowLockManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the application's main frame.
     *
     * @param frame the main application frame
     */
    public void registerMainFrame(JFrame frame) {
        this.mainFrame = frame;
    }

    /**
     * Registers a child window so it participates in window-lock and snap
     * behaviour. The window deregisters itself automatically when closed.
     *
     * @param window the child window to register
     */
    public void registerChildWindow(Window window) {
        children.add(window);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                children.remove(window);
                childLastBounds.remove(window);
            }
        });
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                childLastBounds.put(window, window.getBounds());
            }

            @Override
            public void componentResized(ComponentEvent e) {
                snapResizedChild(window);
            }
        });
        childLastBounds.put(window, window.getBounds());
    }

    /**
     * Enables or disables the window lock.
     *
     * @param locked {@code true} to lock, {@code false} to unlock
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
        notifyListeners();
    }

    /**
     * Returns whether the window lock is currently active.
     *
     * @return {@code true} if locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Registers a listener that is notified whenever the lock state changes.
     *
     * @param listener callback to invoke on lock state change
     */
    public void addLockStateListener(Runnable listener) {
        lockStateListeners.add(listener);
    }

    /**
     * Removes a previously registered lock-state listener.
     *
     * @param listener the listener to remove
     */
    public void removeLockStateListener(Runnable listener) {
        lockStateListeners.remove(listener);
    }

    /**
     * Moves {@code window} to ({@code x}, {@code y}). When the window lock is
     * active and {@code window} is the main frame, all registered child windows
     * are moved by the same delta <em>first</em>, so that the OS compositor
     * encounters them already at their new positions before the main frame itself
     * moves. A {@link Toolkit#sync()} flush is issued afterward to push the
     * native display buffer.
     *
     * @param window the window to move
     * @param x      target screen x coordinate
     * @param y      target screen y coordinate
     */
    public void moveWindowWithLock(Window window, int x, int y) {
        if (locked && window == mainFrame) {
            int dx = x - window.getX();
            int dy = y - window.getY();
            if (dx != 0 || dy != 0) {
                for (Window child : children) {
                    if (child.isShowing()) {
                        child.setLocation(child.getX() + dx, child.getY() + dy);
                    }
                }
            }
        }
        window.setLocation(x, y);
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Returns a (potentially snapped) screen position for a window being
     * dragged to ({@code x}, {@code y}). Each axis is snapped independently
     * to the closest candidate within {@value #SNAP_DISTANCE} pixels.
     * Four snap types are checked per registered window per axis:
     * adjacent-edge (side-by-side / stacked) and same-edge alignment.
     * <p>
     * Snapping is intentionally disabled when the dragging window is the main
     * frame: only child windows snap to the main frame or to each other.
     *
     * @param dragging the window being dragged
     * @param x        proposed screen x coordinate
     * @param y        proposed screen y coordinate
     * @return the snapped position, or the original coordinates if no snap applies
     */
    public Point applySnap(Window dragging, int x, int y) {
        if (dragging == mainFrame) {
            return new Point(x, y);
        }
        int dW = dragging.getWidth();
        int dH = dragging.getHeight();

        int snapX = x;
        int snapY = y;
        int closestX = SNAP_DISTANCE + 1;
        int closestY = SNAP_DISTANCE + 1;

        for (Window other : getOtherWindows(dragging)) {
            Rectangle r = other.getBounds();

            int d;

            d = Math.abs(x - (r.x + r.width));
            if (d < closestX) { closestX = d; snapX = r.x + r.width; }

            d = Math.abs((x + dW) - r.x);
            if (d < closestX) { closestX = d; snapX = r.x - dW; }

            d = Math.abs(x - r.x);
            if (d < closestX) { closestX = d; snapX = r.x; }

            d = Math.abs((x + dW) - (r.x + r.width));
            if (d < closestX) { closestX = d; snapX = r.x + r.width - dW; }

            d = Math.abs(y - (r.y + r.height));
            if (d < closestY) { closestY = d; snapY = r.y + r.height; }

            d = Math.abs((y + dH) - r.y);
            if (d < closestY) { closestY = d; snapY = r.y - dH; }

            d = Math.abs(y - r.y);
            if (d < closestY) { closestY = d; snapY = r.y; }

            d = Math.abs((y + dH) - (r.y + r.height));
            if (d < closestY) { closestY = d; snapY = r.y + r.height - dH; }
        }

        return new Point(snapX, snapY);
    }

    private void snapResizedChild(Window window) {
        if (snappingInProgress.contains(window)) {
            return;
        }
        Rectangle prev = childLastBounds.get(window);
        Rectangle curr = window.getBounds();
        if (prev == null || curr.equals(prev)) {
            childLastBounds.put(window, new Rectangle(curr));
            return;
        }

        boolean leftMoved   = curr.x != prev.x;
        boolean rightMoved  = (curr.x + curr.width)  != (prev.x + prev.width);
        boolean topMoved    = curr.y != prev.y;
        boolean bottomMoved = (curr.y + curr.height) != (prev.y + prev.height);

        int newX      = curr.x;
        int newRight  = curr.x + curr.width;
        int newY      = curr.y;
        int newBottom = curr.y + curr.height;

        List<Integer> vLines = collectVerticalSnapLines(window);
        List<Integer> hLines = collectHorizontalSnapLines(window);

        if (leftMoved) {
            int snapped = snapToNearest(curr.x, vLines);
            if (snapped != curr.x) newX = snapped;
        }
        if (rightMoved) {
            int snapped = snapToNearest(curr.x + curr.width, vLines);
            if (snapped != curr.x + curr.width) newRight = snapped;
        }
        if (topMoved) {
            int snapped = snapToNearest(curr.y, hLines);
            if (snapped != curr.y) newY = snapped;
        }
        if (bottomMoved) {
            int snapped = snapToNearest(curr.y + curr.height, hLines);
            if (snapped != curr.y + curr.height) newBottom = snapped;
        }

        Rectangle snapped = new Rectangle(newX, newY, newRight - newX, newBottom - newY);
        childLastBounds.put(window, new Rectangle(snapped));

        if (!snapped.equals(curr)) {
            snappingInProgress.add(window);
            window.setBounds(snapped);
            snappingInProgress.remove(window);
        }
    }

    private int snapToNearest(int value, List<Integer> lines) {
        int best = value;
        int bestDist = SNAP_DISTANCE + 1;
        for (int line : lines) {
            int d = Math.abs(value - line);
            if (d < bestDist) {
                bestDist = d;
                best = line;
            }
        }
        return best;
    }

    private List<Integer> collectVerticalSnapLines(Window exclude) {
        List<Integer> lines = new ArrayList<>();
        for (Window w : getOtherWindows(exclude)) {
            Rectangle r = w.getBounds();
            lines.add(r.x);
            lines.add(r.x + r.width);
        }
        return lines;
    }

    private List<Integer> collectHorizontalSnapLines(Window exclude) {
        List<Integer> lines = new ArrayList<>();
        for (Window w : getOtherWindows(exclude)) {
            Rectangle r = w.getBounds();
            lines.add(r.y);
            lines.add(r.y + r.height);
        }
        return lines;
    }

    private List<Window> getOtherWindows(Window exclude) {
        List<Window> all = new ArrayList<>();
        if (mainFrame != null && mainFrame != exclude && mainFrame.isShowing()) {
            all.add(mainFrame);
        }
        for (Window w : children) {
            if (w != exclude && w.isShowing()) {
                all.add(w);
            }
        }
        return all;
    }

    private void notifyListeners() {
        lockStateListeners.forEach(Runnable::run);
    }
}

