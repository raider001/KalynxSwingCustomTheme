package com.kalynx.swingtheme.themedcomponents;

import java.io.Serial;

import com.kalynx.swingtheme.BindingLifecycleHelper;
import com.kalynx.swingtheme.ComponentModel;
import com.kalynx.swingtheme.theme.Theme;
import com.kalynx.swingtheme.theme.ThemeManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

/**
 * ThemedComboBox - A simple JComboBox that queries theme colors on-demand
 * Provides clean, automatic theme integration
 */
public class ThemedComboBox<T> extends JComboBox<T> {
    @Serial
    private static final long serialVersionUID = 1L;

    private transient final ThemeManager themeManager;

    private transient ComponentModel<T> valueModel;
    private transient ComponentModel<List<T>> optionsModel;
    private transient BindingLifecycleHelper.ComboBoxBinding<T> binding;

    public ThemedComboBox() {
        super();
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    public ThemedComboBox(T[] items) {
        super(items);
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    public ThemedComboBox(Vector<T> items) {
        super(items);
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    public ThemedComboBox(ComboBoxModel<T> model) {
        super(model);
        this.themeManager = ThemeManager.getInstance();
        initialize();
    }

    private void initialize() {
        setFont(new Font("Segoe UI", Font.PLAIN, themeManager.scale(12)));
        setRenderer(new ThemedComboBoxRenderer());
        setOpaque(true);
        setFocusable(true);
        setUI(new ThemedComboBoxUI());
        applyTheme();
    }

    private void applyTheme() {
        Theme theme = themeManager.getCurrentTheme();
        setBackground(theme.getInputBackground());
        setForeground(theme.getForegroundColor());
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(
            themeManager.scale(2), themeManager.scale(4),
            themeManager.scale(2), themeManager.scale(4)));
    }

    @Override
    public void updateUI() {
        if (themeManager != null) {
            setUI(new ThemedComboBoxUI());
            SwingUtilities.invokeLater(this::applyTheme);
        } else {
            super.updateUI();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (themeManager != null) {
            Theme theme = themeManager.getCurrentTheme();
            setBackground(theme.getInputBackground());
            setForeground(theme.getForegroundColor());
        }
        super.paintComponent(g);
    }

    // -------------------------------------------------------------------------
    // Custom UI
    // -------------------------------------------------------------------------

    private class ThemedComboBoxUI extends BasicComboBoxUI {

        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼") {
                @Override
                public void paintComponent(Graphics g) {
                    Theme theme = themeManager.getCurrentTheme();
                    g.setColor(theme.getInputBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(theme.getForegroundColor());
                    g.setFont(getFont());
                    FontMetrics fm = g.getFontMetrics();
                    String text = getText();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g.drawString(text, x, y);
                }
            };
            button.setName("ComboBox.arrowButton");
            button.setFont(new Font("Segoe UI", Font.PLAIN, themeManager.scale(10)));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setOpaque(false);
            button.setMargin(new Insets(0, 0, 0, 0));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return button;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Theme theme = themeManager.getCurrentTheme();
            g.setColor(theme.getInputBackground());
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected ComboPopup createPopup() {
            return new ThemedComboPopup((JComboBox<Object>) comboBox);
        }
    }

    // -------------------------------------------------------------------------
    // Custom popup — JWindow-based so the Windows L&F never touches it
    // -------------------------------------------------------------------------

    private class ThemedComboPopup extends BasicComboPopup {
        @Serial
        private static final long serialVersionUID = 1L;

        private JWindow popupWindow;
        private AWTEventListener outsideClickListener;

        ThemedComboPopup(JComboBox<Object> combo) {
            super(combo);
        }

        @Override
        public void show() {
            if (popupWindow == null) buildWindow();

            int sel = comboBox.getSelectedIndex();
            if (sel >= 0) {
                list.setSelectedIndex(sel);
                list.ensureIndexIsVisible(sel);
            }

            positionAndShow();

            outsideClickListener = event -> {
                if (event instanceof MouseEvent me && me.getID() == MouseEvent.MOUSE_PRESSED) {
                    if (isVisible() && !popupWindow.getBounds().contains(me.getLocationOnScreen())) {
                        SwingUtilities.invokeLater(this::hide);
                    }
                }
            };
            Toolkit.getDefaultToolkit().addAWTEventListener(
                    outsideClickListener, AWTEvent.MOUSE_EVENT_MASK);
        }

        @Override
        public void hide() {
            if (popupWindow != null) popupWindow.setVisible(false);
            if (outsideClickListener != null) {
                Toolkit.getDefaultToolkit().removeAWTEventListener(outsideClickListener);
                outsideClickListener = null;
            }
        }

        @Override
        public boolean isVisible() {
            return popupWindow != null && popupWindow.isVisible();
        }

        private void buildWindow() {
            Window owner = SwingUtilities.getWindowAncestor(comboBox);
            popupWindow = new JWindow(owner);
            popupWindow.setType(Window.Type.POPUP);
            popupWindow.setFocusableWindowState(false);

            Theme theme = themeManager.getCurrentTheme();
            Color bg = theme.getInputBackground();

            list.setBackground(bg);
            list.setForeground(theme.getForegroundColor());
            list.setSelectionBackground(theme.getAccentColor());
            list.setSelectionForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(list,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            ThemedScrollBar vScrollBar = new ThemedScrollBar(JScrollBar.VERTICAL);
            scrollPane.setVerticalScrollBar(vScrollBar);
            scrollPane.setBackground(bg);
            scrollPane.getViewport().setBackground(bg);
            scrollPane.setOpaque(true);
            scrollPane.setBorder(BorderFactory.createLineBorder(theme.getBorderColor(), 1));

            popupWindow.setContentPane(scrollPane);

            list.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    int idx = list.locationToIndex(e.getPoint());
                    if (idx >= 0) {
                        comboBox.setSelectedIndex(idx);
                        hide();
                    }
                }
            });
        }

        private void positionAndShow() {
            Point p = comboBox.getLocationOnScreen();
            int w = comboBox.getWidth();
            int rows = Math.min(comboBox.getMaximumRowCount(), list.getModel().getSize());
            int cellH = estimateCellHeight();
            int h = Math.max(rows * cellH, themeManager.scale(40));

            int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
            int yBelow = p.y + comboBox.getHeight();
            int y = (yBelow + h <= screenH) ? yBelow : p.y - h;

            popupWindow.setBounds(p.x, y, w, h);
            popupWindow.setVisible(true);
            popupWindow.toFront();
        }

        private int estimateCellHeight() {
            int fixed = list.getFixedCellHeight();
            if (fixed > 0) return fixed;
            if (list.getModel().getSize() == 0) return themeManager.scale(24);
            Component c = list.getCellRenderer().getListCellRendererComponent(
                    list, list.getModel().getElementAt(0), 0, false, false);
            return Math.max(c.getPreferredSize().height, themeManager.scale(24));
        }
    }

    // -------------------------------------------------------------------------
    // Themed renderer
    // -------------------------------------------------------------------------

    private class ThemedComboBoxRenderer extends DefaultListCellRenderer {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            Theme theme = themeManager.getCurrentTheme();

            if (list != null) {
                list.setBackground(theme.getInputBackground());
                list.setForeground(theme.getForegroundColor());
                list.setSelectionBackground(theme.getAccentColor());
                list.setSelectionForeground(Color.WHITE);
            }

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Segoe UI", Font.PLAIN, themeManager.scale(12)));
            label.setBorder(BorderFactory.createEmptyBorder(
                themeManager.scale(4), themeManager.scale(8),
                themeManager.scale(4), themeManager.scale(8)));
            label.setOpaque(true);

            if (isSelected) {
                label.setBackground(theme.getAccentColor());
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(theme.getInputBackground());
                label.setForeground(theme.getForegroundColor());
            }

            return label;
        }
    }

    // -------------------------------------------------------------------------
    // Model binding
    // -------------------------------------------------------------------------

    public void bindTo(ComponentModel<T> valueModel, ComponentModel<List<T>> optionsModel) {
        unbind();
        this.valueModel = valueModel;
        this.optionsModel = optionsModel;
        binding = BindingLifecycleHelper.setupComboBoxBinding(valueModel, optionsModel, this);
        BindingLifecycleHelper.setupAutoUnbind(this, this::unbind);
    }

    public void bindTo(ComponentModel<T> valueModel) {
        bindTo(valueModel, null);
    }

    public void unbind() {
        if (binding != null) {
            BindingLifecycleHelper.unbindComboBox(
                valueModel, optionsModel,
                binding.valueChangeListener, binding.optionsChangeListener,
                this, binding.selectionListener);
        }
        valueModel = null;
        optionsModel = null;
        binding = null;
    }

    public void addActionListener(Runnable action) {
        addActionListener(_ -> action.run());
    }
}
