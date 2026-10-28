package com.quizmaster.components;

import com.quizmaster.service.AudioService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * High-emphasis primary CTA button with rounded corners, amber background,
 * dark high-contrast typography, hover animations, and tactile press displacement.
 */
public class PrimaryButton extends JButton {

    private boolean isHovered = false;
    private boolean isPressed = false;
    private Color baseColor = AppTheme.ACCENT_AMBER;
    private Color hoverColor = AppTheme.ACCENT_AMBER_HOVER;
    private Color textColor = AppTheme.TEXT_DARK;

    public PrimaryButton(String text) {
        super(text);
        initButton();
    }

    private void initButton() {
        setFont(AppTheme.FONT_BODY_BOLD);
        setForeground(textColor);
        setFocusPainted(false);
        setRequestFocusEnabled(false); // clicking must not leave a focus ring; Tab still works
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(getPreferredSize().width + 36, 44));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    AudioService.playClick();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setCustomColors(Color base, Color hover, Color text) {
        this.baseColor = base;
        this.hoverColor = hover;
        this.textColor = text;
        setForeground(textColor);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        AppTheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int yOffset = isPressed ? 1 : 0;

        Color bg = !isEnabled() ? AppTheme.BG_CARD_HOVER :
                   isPressed ? hoverColor.darker() :
                   isHovered ? hoverColor : baseColor;

        // Fill rounded rect
        g2.setColor(bg);
        g2.fillRoundRect(0, yOffset, w, h - yOffset, AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);

        // Subtle top highlight border
        if (isEnabled() && !isPressed) {
            g2.setColor(AppTheme.withAlpha(Color.WHITE, 40));
            g2.drawRoundRect(0, 0, w - 1, h - 1, AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
        }

        // Draw centered text
        g2.setFont(getFont());
        g2.setColor(isEnabled() ? textColor : AppTheme.TEXT_MUTED);
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent() + yOffset;
        g2.drawString(getText(), tx, ty);

        // Keyboard focus ring (mouse clicks do not take focus, so it only shows for Tab navigation)
        if (isFocusOwner() && isEnabled()) {
            g2.setColor(AppTheme.ACCENT_BLUE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
        }

        g2.dispose();
    }
}
