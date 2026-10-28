package com.quizmaster.components;

import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * Animated circular score progress meter.
 * Renders a track ring and an active amber arc with round stroke caps,
 * plus centered percentage text and sub-label ("12 of 15 Correct").
 */
public class CircularScoreMeter extends JComponent {

    private int targetPercent = 0;
    private float currentPercent = 0f;
    private String subLabel = "";
    private Timer animationTimer;
    private final int strokeWidth = 14;

    public CircularScoreMeter() {
        setPreferredSize(new Dimension(220, 220));
        setOpaque(false);
    }

    public void setScore(int percent, String subLabel) {
        this.targetPercent = Math.max(0, Math.min(100, percent));
        this.subLabel = subLabel != null ? subLabel : "";
        startAnimation();
    }

    public void setScoreImmediate(int percent, String subLabel) {
        this.targetPercent = Math.max(0, Math.min(100, percent));
        this.currentPercent = targetPercent;
        this.subLabel = subLabel != null ? subLabel : "";
        repaint();
    }

    private void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        currentPercent = 0f;
        int frames = 35;
        float step = targetPercent / (float) frames;

        animationTimer = new Timer(18, e -> {
            currentPercent += step;
            if (currentPercent >= targetPercent) {
                currentPercent = targetPercent;
                animationTimer.stop();
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        AppTheme.applyQualityRendering(g2);

        int size = Math.min(getWidth(), getHeight()) - strokeWidth * 2;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        // Background track ring
        g2.setColor(AppTheme.BG_CARD_HOVER);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(x, y, size, size);

        // Active animated arc
        if (currentPercent > 0) {
            // BUG-15: colour follows the *animated* value so the ring transitions
            // red → blue → amber as it grows instead of snapping to the final hue.
            Color arcColor;
            if (currentPercent >= 80f) {
                arcColor = AppTheme.ACCENT_AMBER;
            } else if (currentPercent >= 60f) {
                arcColor = AppTheme.ACCENT_BLUE;
            } else {
                arcColor = AppTheme.ACCENT_RED;
            }

            g2.setColor(arcColor);
            float extent = -(currentPercent / 100.0f) * 360.0f;
            // Arc2D: start at 90 (12 o'clock), sweep clockwise with negative extent
            Arc2D.Float arc = new Arc2D.Float(x, y, size, size, 90f, extent, Arc2D.OPEN);
            g2.draw(arc);
        }

        // Center Percentage Text
        String percentStr = Math.round(currentPercent) + "%";
        g2.setFont(AppTheme.FONT_SCORE_BIG);
        FontMetrics fmScore = g2.getFontMetrics();
        int scoreWidth = fmScore.stringWidth(percentStr);

        g2.setFont(AppTheme.FONT_SMALL_BOLD);
        FontMetrics fmSub = g2.getFontMetrics();
        int subWidth = fmSub.stringWidth(subLabel);

        int totalTextHeight = fmScore.getAscent() + fmSub.getHeight() + 4;
        int startY = (getHeight() - totalTextHeight) / 2 + fmScore.getAscent();

        // Draw Score String
        g2.setFont(AppTheme.FONT_SCORE_BIG);
        g2.setColor(AppTheme.TEXT_PRIMARY);
        g2.drawString(percentStr, (getWidth() - scoreWidth) / 2, startY);

        // Draw Subtitle ("12 of 15 Correct")
        if (!subLabel.isEmpty()) {
            g2.setFont(AppTheme.FONT_SMALL_BOLD);
            g2.setColor(AppTheme.TEXT_SECONDARY);
            g2.drawString(subLabel, (getWidth() - subWidth) / 2, startY + fmSub.getHeight() + 4);
        }

        g2.dispose();
    }
}
