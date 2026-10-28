package com.quizmaster.util;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Slim rounded scroll bar that matches the dark theme (the system look-and-feel
 * otherwise draws bright white bars that clash with every screen).
 */
public class DarkScrollBarUI extends BasicScrollBarUI {

    private static final int THICKNESS = 10;

    /** Applies the dark style to both bars of a scroll pane. */
    public static void style(JScrollPane pane) {
        for (JScrollBar bar : new JScrollBar[]{pane.getVerticalScrollBar(), pane.getHorizontalScrollBar()}) {
            bar.setUI(new DarkScrollBarUI());
            bar.setOpaque(false);
            bar.setPreferredSize(bar.getOrientation() == Adjustable.VERTICAL
                    ? new Dimension(THICKNESS, 0) : new Dimension(0, THICKNESS));
        }
    }

    @Override
    protected void configureScrollBarColors() {
        thumbColor = AppTheme.BORDER_SUBTLE;
        trackColor = AppTheme.BG_CANVAS;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return zeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return zeroButton();
    }

    private static JButton zeroButton() {
        JButton b = new JButton();
        Dimension zero = new Dimension(0, 0);
        b.setPreferredSize(zero);
        b.setMinimumSize(zero);
        b.setMaximumSize(zero);
        b.setFocusable(false);
        return b;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // transparent track: the surface behind the bar shows through
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2 = (Graphics2D) g.create();
        AppTheme.applyQualityRendering(g2);
        g2.setColor(isThumbRollover() || isDragging ? AppTheme.TEXT_MUTED : thumbColor);
        g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, THICKNESS, THICKNESS);
        g2.dispose();
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(THICKNESS, 28);
    }
}
