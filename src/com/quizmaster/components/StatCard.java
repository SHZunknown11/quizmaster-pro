package com.quizmaster.components;

import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * 2x2 KPI Stat Card widget displaying a large metric value, title label,
 * and optional vector badge (such as flame or trophy).
 */
public class StatCard extends RoundedCard {

    public enum IconType {
        NONE, FLAME, TROPHY, TARGET, CHART
    }

    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private IconType iconType = IconType.NONE;
    private Color accentColor = AppTheme.TEXT_PRIMARY;

    public StatCard(String title, String value, Color valueColor, IconType iconType) {
        super(AppTheme.CARD_RADIUS, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
        this.iconType = iconType;
        this.accentColor = valueColor != null ? valueColor : AppTheme.TEXT_PRIMARY;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 18, 16, 18));

        // Top bar: title + icon (if any)
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_SUBHEADING);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        topRow.add(titleLabel, BorderLayout.WEST);

        if (iconType != IconType.NONE) {
            JPanel iconPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    AppTheme.applyQualityRendering(g2);
                    drawVectorIcon(g2, iconType, getWidth(), getHeight());
                    g2.dispose();
                }

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(22, 22);
                }
            };
            iconPanel.setOpaque(false);
            topRow.add(iconPanel, BorderLayout.EAST);
        }

        add(topRow, BorderLayout.NORTH);

        // Center / Bottom: Large KPI Number
        valueLabel = new JLabel(value);
        valueLabel.setFont(AppTheme.FONT_KPI_NUM);
        valueLabel.setForeground(this.accentColor);
        valueLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setValueColor(Color color) {
        this.accentColor = color;
        valueLabel.setForeground(color);
    }

    private void drawVectorIcon(Graphics2D g2, IconType type, int w, int h) {
        if (type == IconType.FLAME) {
            // Draw clean flame vector in amber/orange
            GeneralPath flame = new GeneralPath();
            flame.moveTo(w * 0.50, h * 0.05);
            flame.curveTo(w * 0.65, h * 0.30, w * 0.85, h * 0.50, w * 0.85, h * 0.70);
            flame.curveTo(w * 0.85, h * 0.95, w * 0.15, h * 0.95, w * 0.15, h * 0.70);
            flame.curveTo(w * 0.15, h * 0.55, w * 0.35, h * 0.35, w * 0.40, h * 0.20);
            flame.curveTo(w * 0.45, h * 0.40, w * 0.60, h * 0.50, w * 0.55, h * 0.65);
            flame.curveTo(w * 0.50, h * 0.55, w * 0.45, h * 0.40, w * 0.50, h * 0.05);
            flame.closePath();

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0xF5, 0x9E, 0x0B),
                    0, h, new Color(0xEF, 0x44, 0x44)
            );
            g2.setPaint(gp);
            g2.fill(flame);
        } else if (type == IconType.TROPHY) {
            g2.setColor(AppTheme.ACCENT_AMBER);
            g2.fillRoundRect(w / 4, h / 6, w / 2, h / 3, 4, 4);
            g2.fillRect(w * 4 / 10, h / 2, w / 5, h / 4);
            g2.fillRoundRect(w / 4, h * 3 / 4, w / 2, h / 8, 4, 4);
        }
    }
}
