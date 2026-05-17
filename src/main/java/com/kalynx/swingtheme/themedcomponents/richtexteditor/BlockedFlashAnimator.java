package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import javax.swing.Timer;

/**
 * Drives the red fade-in / fade-out alpha curve used to flag a blocked
 * command. Pure animation state — the owning component (or {@code LayerUI})
 * supplies a repaint callback and reads {@link #getAlpha()} during paint.
 *
 * <p>The wave is a symmetric triangle that ramps up to a peak, then back
 * down, over a fixed duration. Calling {@link #flash()} while a previous
 * animation is still running cancels and restarts it.</p>
 */
public final class BlockedFlashAnimator {

    private static final int FRAME_DELAY_MS = 16;
    private static final int DEFAULT_DURATION_MS = 400;
    private static final float DEFAULT_PEAK_ALPHA = 0.55f;

    private final int durationMs;
    private final float peakAlpha;
    private final Runnable onTick;
    private final Timer timer;
    private long startTime;
    private float alpha;

    /**
     * Creates an animator using the default 400ms duration and 0.55 peak alpha.
     *
     * @param onTick callback invoked on every animation frame (typically
     *               {@code component::repaint})
     */
    public BlockedFlashAnimator(Runnable onTick) {
        this(onTick, DEFAULT_DURATION_MS, DEFAULT_PEAK_ALPHA);
    }

    /**
     * @param onTick      callback invoked on every animation frame
     * @param durationMs  total animation length in milliseconds
     * @param peakAlpha   alpha at the midpoint of the wave (0..1)
     */
    public BlockedFlashAnimator(Runnable onTick, int durationMs, float peakAlpha) {
        this.onTick = onTick;
        this.durationMs = durationMs;
        this.peakAlpha = peakAlpha;
        this.timer = new Timer(FRAME_DELAY_MS, _ -> tick());
        this.timer.setRepeats(true);
    }

    /**
     * Starts (or restarts) the flash animation.
     */
    public void flash() {
        if (timer.isRunning()) {
            timer.stop();
        }
        startTime = System.currentTimeMillis();
        alpha = 0f;
        timer.start();
    }

    /**
     * @return current alpha in the range 0..{@code peakAlpha}; 0 when no flash
     *         is active
     */
    public float getAlpha() {
        return alpha;
    }

    private void tick() {
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1f, (float) elapsed / durationMs);
        float wave = progress < 0.5f ? progress * 2f : (1f - progress) * 2f;
        alpha = wave * peakAlpha;
        if (onTick != null) {
            onTick.run();
        }
        if (progress >= 1f) {
            alpha = 0f;
            timer.stop();
            if (onTick != null) {
                onTick.run();
            }
        }
    }
}

