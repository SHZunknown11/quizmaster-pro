package com.quizmaster.components;

import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * A sleek container panel with anti-aliased rounded corners, subtle border,
 * and high-contrast dark surface fill.
 */
public class RoundedCard extends JPanel {

    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private int borderWidth;

    public RoundedCard() {
        this(AppTheme.CARD_RADIUS, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
    }

    public RoundedCard(int cornerRadius, Color backgroundColor, Color borderColor, int borderWidth) {
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        setOpaque(false);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        AppTheme.applyQualityRendering(g2);

        int width = getWidth();
        int height = getHeight();

        // Fill background
        if (backgroundColor != null) {
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
        }

        // Draw border
        if (borderColor != null && borderWidth > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            // Offset by half stroke for crisp alignment
            float offset = borderWidth / 2.0f;
            g2.drawRoundRect((int) offset, (int) offset,
                    width - borderWidth, height - borderWidth,
                    cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
