package com.quizmaster.components;

import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Capsule/stadium shaped badge pill used for stats breakdown:
 * e.g., "✓ 8 Correct", "✕ 1 Wrong", "→ 1 Skipped", "Grade A".
 */
public class PillBadge extends JComponent {

    private String text;
    private Color accentColor;
    private Color badgeBackground;
    private Font font = AppTheme.FONT_SMALL_BOLD;

    public PillBadge(String text, Color accentColor) {
        this.text = text;
        this.accentColor = accentColor;
        this.badgeBackground = AppTheme.withAlpha(accentColor, 35);
        setOpaque(false);
    }

    public void setText(String text) {
        this.text = text;
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
        return new Dimension(textW + 24, 28);
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

        // Centered Text
        g2.setFont(font);
        g2.setColor(accentColor);
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);

        g2.dispose();
    }
}
