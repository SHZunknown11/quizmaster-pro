package com.quizmaster.components;

import com.quizmaster.service.AudioService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Secondary outlined button with dark surface, hairline border,
 * hover glow, and smooth press tactile displacement.
 */
public class SecondaryButton extends JButton {

    private boolean isHovered = false;
    private boolean isPressed = false;

    public SecondaryButton(String text) {
        super(text);
        initButton();
    }

    private void initButton() {
        setFont(AppTheme.FONT_BODY_BOLD);
        setForeground(AppTheme.TEXT_PRIMARY);
        setFocusPainted(false);
        setRequestFocusEnabled(false); // clicking must not leave a focus ring; Tab still works
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(getPreferredSize().width + 32, 44));

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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        AppTheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int yOffset = isPressed ? 1 : 0;

        Color bg = !isEnabled() ? AppTheme.BG_SURFACE_DARK :
                   isHovered ? AppTheme.BG_CARD_HOVER : AppTheme.BG_CARD;
        Color border = isHovered ? AppTheme.BORDER_ACTIVE : AppTheme.BORDER_SUBTLE;

        // Background
        g2.setColor(bg);
        g2.fillRoundRect(0, yOffset, w, h - yOffset, AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);

        // Border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, yOffset, w - 1, h - 1 - yOffset, AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);

        // Centered Text
        g2.setFont(getFont());
        g2.setColor(isEnabled() ? (isHovered ? AppTheme.TEXT_PRIMARY : AppTheme.TEXT_SECONDARY) : AppTheme.TEXT_MUTED);
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
