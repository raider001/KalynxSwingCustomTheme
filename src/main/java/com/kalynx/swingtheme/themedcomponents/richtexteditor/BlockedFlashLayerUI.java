package com.kalynx.swingtheme.themedcomponents.richtexteditor;

import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.plaf.LayerUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serial;

/**
 * {@link LayerUI} that overlays a brief red fade-in / fade-out flash on top of
 * the wrapped {@link JComponent} to signal that a requested command was
 * blocked. Use this to give arbitrary components (combo boxes, panels, etc.)
 * the same blocked-feedback behaviour that {@link RichTextEditorButton}
 * provides natively.
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * BlockedFlashLayerUI ui = new BlockedFlashLayerUI();
 * JLayer<JComboBox<Foo>> wrapped = new JLayer<>(combo, ui);
 * parent.add(wrapped);
 * // ...later, on a blocked attempt:
 * ui.flash();
 * }</pre>
 */
public class BlockedFlashLayerUI extends LayerUI<JComponent> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient ThemeManager themeManager = ThemeManager.getInstance();
    private final transient BlockedFlashAnimator animator;
    private transient JLayer<? extends JComponent> attachedLayer;

    public BlockedFlashLayerUI() {
        this.animator = new BlockedFlashAnimator(() -> {
            if (attachedLayer != null) {
                attachedLayer.repaint();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void installUI(JComponent c) {
        super.installUI(c);
        if (c instanceof JLayer<?>) {
            attachedLayer = (JLayer<? extends JComponent>) c;
        }
    }

    @Override
    public void uninstallUI(JComponent c) {
        if (attachedLayer == c) {
            attachedLayer = null;
        }
        super.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);

        float alpha = animator.getAlpha();
        if (alpha <= 0f) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = themeManager.getCurrentTheme().getErrorColor();
            int a = Math.max(0, Math.min(255, Math.round(alpha * 255f)));
            g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), a));
            int radius = themeManager.scale(6);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), radius, radius);
        } finally {
            g2.dispose();
        }
    }

    /**
     * Starts (or restarts) the blocked-flash animation over the wrapped
     * component.
     */
    public void flash() {
        animator.flash();
    }
}

