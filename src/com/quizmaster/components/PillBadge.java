package com.quizmaster.components;

import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Capsule/stadium shaped badge pill used for stats breakdown:
 * e.g., "8 Correct", "1 Wrong", "1 Skipped", "Grade A".
 * An optional vector glyph is drawn before the text — the UI font has no
 * check/cross characters, so they would otherwise render as empty boxes.
 */
public class PillBadge extends JComponent {

    public enum Glyph { NONE, CHECK, CROSS, ARROW }

    private static final int GLYPH_SIZE = 9;
    private static final int GLYPH_GAP = 6;

    private String text;
    private Color accentColor;
    private Color badgeBackground;
    private Glyph glyph;
    private Font font = AppTheme.FONT_SMALL_BOLD;

    public PillBadge(String text, Color accentColor) {
        this(text, accentColor, Glyph.NONE);
    }

    public PillBadge(String text, Color accentColor, Glyph glyph) {
        this.text = text;
        this.accentColor = accentColor;
        this.glyph = glyph;
        this.badgeBackground = AppTheme.withAlpha(accentColor, 35);
        setOpaque(false);
    }

    public void setText(String text) {
        this.text = text;
        revalidate();
        repaint();
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
        this.badgeBackground = AppTheme.withAlpha(accentColor, 35);
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(font);
        int textW = fm.stringWidth(text);
        int glyphW = glyph == Glyph.NONE ? 0 : GLYPH_SIZE + GLYPH_GAP;
        return new Dimension(textW + glyphW + 24, 28);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        AppTheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int r = h; // capsule shape

        // Background pill
        g2.setColor(badgeBackground);
        g2.fillRoundRect(0, 0, w, h, r, r);

        // Subtle 1px border
        g2.setColor(AppTheme.withAlpha(accentColor, 120));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);

        // Glyph + text, centred together
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);
        int glyphW = glyph == Glyph.NONE ? 0 : GLYPH_SIZE + GLYPH_GAP;
        int startX = (w - (glyphW + textW)) / 2;

        g2.setColor(accentColor);
        if (glyph != Glyph.NONE) {
            drawGlyph(g2, startX, (h - GLYPH_SIZE) / 2);
        }

        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, startX + glyphW, ty);

        g2.dispose();
    }

    private void drawGlyph(Graphics2D g2, int x, int y) {
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int s = GLYPH_SIZE;
        switch (glyph) {
            case CHECK:
                g2.drawLine(x, y + s / 2 + 1, x + s / 3 + 1, y + s - 1);
                g2.drawLine(x + s / 3 + 1, y + s - 1, x + s, y + 1);
                break;
            case CROSS:
                g2.drawLine(x + 1, y + 1, x + s - 1, y + s - 1);
                g2.drawLine(x + s - 1, y + 1, x + 1, y + s - 1);
                break;
            case ARROW:
                g2.drawLine(x, y + s / 2, x + s, y + s / 2);
                g2.drawLine(x + s - 4, y + s / 2 - 4, x + s, y + s / 2);
                g2.drawLine(x + s - 4, y + s / 2 + 4, x + s, y + s / 2);
                break;
            default:
                break;
        }
    }
}
