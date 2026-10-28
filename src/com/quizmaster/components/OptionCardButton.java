package com.quizmaster.components;

import com.quizmaster.service.AudioService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Interactive MCQ Choice card displaying choice letter badge [A-D],
 * option prompt text, and feedback state transitions (Default, Hover,
 * Selected, Correct, Wrong, Eliminated).
 */
public class OptionCardButton extends JPanel {

    public enum CardState {
        DEFAULT, SELECTED, CORRECT, WRONG, ELIMINATED
    }

    private final char letter;
    private String text;
    private CardState state = CardState.DEFAULT;
    private boolean isHovered = false;
    private Runnable onSelectCallback;

    public OptionCardButton(char letter, String text, Runnable onSelectCallback) {
        this.letter = letter;
        this.text = text;
        this.onSelectCallback = onSelectCallback;

        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(500, 56));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (state != CardState.ELIMINATED && isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (state != CardState.ELIMINATED && isEnabled() && onSelectCallback != null) {
                    AudioService.playClick();
                    onSelectCallback.run();
                }
            }
        });
    }

    public void setState(CardState state) {
        this.state = state;
        if (state == CardState.ELIMINATED) {
            setEnabled(false);
            setCursor(Cursor.getDefaultCursor());
        } else {
            setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        repaint();
    }

    public CardState getState() {
        return state;
    }

    public void setText(String text) {
        this.text = text;
        repaint();
    }

    public String getText() {
        return text;
    }

    public char getLetter() {
        return letter;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        AppTheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();
        int r = 12;

        Color bg = AppTheme.BG_CARD;
        Color border = AppTheme.BORDER_SUBTLE;
        int borderWidth = 1;
        Color badgeBg = AppTheme.BG_CANVAS;
        Color badgeText = AppTheme.TEXT_SECONDARY;
        Color textColor = AppTheme.TEXT_PRIMARY;

        switch (state) {
            case SELECTED:
                bg = AppTheme.withAlpha(AppTheme.ACCENT_AMBER, 25);
                border = AppTheme.ACCENT_AMBER;
                borderWidth = 2;
                badgeBg = AppTheme.ACCENT_AMBER;
                badgeText = AppTheme.TEXT_DARK;
                textColor = AppTheme.TEXT_PRIMARY;
                break;
            case CORRECT:
                bg = AppTheme.withAlpha(AppTheme.ACCENT_GREEN, 30);
                border = AppTheme.ACCENT_GREEN;
                borderWidth = 2;
                badgeBg = AppTheme.ACCENT_GREEN;
                badgeText = Color.WHITE;
                textColor = AppTheme.ACCENT_GREEN;
                break;
            case WRONG:
                bg = AppTheme.withAlpha(AppTheme.ACCENT_RED, 30);
                border = AppTheme.ACCENT_RED;
                borderWidth = 2;
                badgeBg = AppTheme.ACCENT_RED;
                badgeText = Color.WHITE;
                textColor = AppTheme.ACCENT_RED;
                break;
            case ELIMINATED:
                bg = AppTheme.withAlpha(AppTheme.BG_SURFACE_DARK, 180);
                border = AppTheme.withAlpha(AppTheme.BORDER_SUBTLE, 60);
                badgeBg = AppTheme.withAlpha(AppTheme.BG_SURFACE_DARK, 180);
                badgeText = AppTheme.TEXT_MUTED;
                textColor = AppTheme.TEXT_MUTED;
                break;
            case DEFAULT:
            default:
                if (isHovered && isEnabled()) {
                    bg = AppTheme.BG_CARD_HOVER;
                    border = AppTheme.BORDER_ACTIVE;
                    badgeBg = AppTheme.withAlpha(AppTheme.ACCENT_AMBER, 40);
                    badgeText = AppTheme.ACCENT_AMBER;
                }
                break;
        }

        // Draw card background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, r, r);

        // Draw border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(borderWidth));
        int offset = borderWidth / 2;
        g2.drawRoundRect(offset, offset, w - borderWidth, h - borderWidth, r, r);

        // Draw Letter Badge [A-D]
        int badgeSize = 32;
        int badgeX = 16;
        int badgeY = (h - badgeSize) / 2;
        g2.setColor(badgeBg);
        g2.fillRoundRect(badgeX, badgeY, badgeSize, badgeSize, 8, 8);

        g2.setColor(badgeText);
        g2.setFont(AppTheme.FONT_BODY_BOLD);
        FontMetrics fmBadge = g2.getFontMetrics();
        String letterStr = String.valueOf(letter);
        int lx = badgeX + (badgeSize - fmBadge.stringWidth(letterStr)) / 2;
        int ly = badgeY + (badgeSize - fmBadge.getHeight()) / 2 + fmBadge.getAscent();
        g2.drawString(letterStr, lx, ly);

        // Draw Option Text
        g2.setFont(AppTheme.FONT_BODY);
        g2.setColor(textColor);
        FontMetrics fmText = g2.getFontMetrics();
        int textX = badgeX + badgeSize + 16;
        int textY = (h - fmText.getHeight()) / 2 + fmText.getAscent();

        // Strikethrough if eliminated
        if (state == CardState.ELIMINATED) {
            g2.drawString(text, textX, textY);
            int tw = fmText.stringWidth(text);
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.drawLine(textX, textY - fmText.getAscent() / 3, textX + tw, textY - fmText.getAscent() / 3);
        } else {
            g2.drawString(text, textX, textY);
        }

        g2.dispose();
    }
}
