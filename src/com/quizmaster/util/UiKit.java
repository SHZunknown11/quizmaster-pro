package com.quizmaster.util;

import javax.swing.*;
import java.awt.*;

/**
 * Small layout helpers shared by the screens.
 */
public final class UiKit {

    private UiKit() {}

    /**
     * Wraps a fixed-size component so a BorderLayout.EAST/WEST slot cannot stretch
     * it to the height of its neighbour (e.g. a two-line title next to a button).
     */
    public static JPanel verticallyCentered(JComponent c) {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.add(c);
        return wrap;
    }

    /**
     * A panel that always matches the width of its scroll-pane viewport, so wrapped
     * content never produces a horizontal scroll bar, while still scrolling
     * vertically when the window is short.
     */
    public static class ViewportWidthPanel extends JPanel implements Scrollable {

        public ViewportWidthPanel() {
            setOpaque(false);
        }

        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) { return 96; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() {
            // fill the viewport when content is shorter, scroll when it is taller
            return getParent() instanceof JViewport
                    && getParent().getHeight() > getPreferredSize().height;
        }
    }
}
