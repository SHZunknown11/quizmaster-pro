package com.quizmaster.components;

import com.quizmaster.service.AudioService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Modern input field with leading vector icons, interactive focus glow,
 * clean typography, and optional password show/hide eye toggle.
 */
public class AuthInputField extends JPanel {

    public enum IconType { USER, LOCK, BADGE }

    private final String labelText;
    private final IconType iconType;
    private final boolean isPassword;

    private JTextField textField;
    private JPasswordField passwordField;
    private boolean isFocused = false;
    private boolean showPassword = false;

    public AuthInputField(String labelText, IconType iconType, boolean isPassword, String initialValue) {
        this.labelText = labelText;
        this.iconType = iconType;
        this.isPassword = isPassword;

        setLayout(new BorderLayout(0, 6));
        setOpaque(false);
        setPreferredSize(new Dimension(380, 68));
        setMaximumSize(new Dimension(380, 68));
        setMinimumSize(new Dimension(380, 68));

        // 1. Top Label
        JLabel label = new JLabel(labelText.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(AppTheme.TEXT_SECONDARY);
        add(label, BorderLayout.NORTH);

        // 2. Input Box Container with custom painting
        JPanel inputBox = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);

                int w = getWidth();
                int h = getHeight();
                int r = 10;

                // Input Background
                g2.setColor(AppTheme.BG_INPUT);
                g2.fillRoundRect(0, 0, w, h, r, r);

                // Focus Glow / Border
                if (isFocused) {
                    // Soft amber outer glow
                    g2.setColor(AppTheme.withAlpha(AppTheme.ACCENT_AMBER, 50));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);

                    // Solid amber border
                    g2.setColor(AppTheme.ACCENT_AMBER);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);
                } else {
                    g2.setColor(AppTheme.BORDER_SUBTLE);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);
                }

                g2.dispose();
            }
        };
        inputBox.setOpaque(false);
        inputBox.setPreferredSize(new Dimension(380, 42));
        inputBox.setBorder(new EmptyBorder(0, 12, 0, 10));

        // Left Icon Widget
        JPanel leftIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                Color iconColor = isFocused ? AppTheme.ACCENT_AMBER : AppTheme.TEXT_MUTED;
                drawLeadingIcon(g2, iconType, getWidth(), getHeight(), iconColor);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() { return new Dimension(20, 20); }
        };
        leftIcon.setOpaque(false);
        inputBox.add(leftIcon, BorderLayout.WEST);

        // Center Input Element
        FocusListener focusListener = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                inputBox.repaint();
                leftIcon.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                inputBox.repaint();
                leftIcon.repaint();
            }
        };

        if (isPassword) {
            passwordField = new JPasswordField(initialValue);
            passwordField.setFont(AppTheme.FONT_BODY);
            passwordField.setForeground(AppTheme.TEXT_PRIMARY);
            passwordField.setCaretColor(AppTheme.ACCENT_AMBER);
            passwordField.setOpaque(false);
            passwordField.setBorder(null);
            passwordField.setEchoChar('•');
            passwordField.addFocusListener(focusListener);
            inputBox.add(passwordField, BorderLayout.CENTER);

            // Right: Eye toggle button for password visibility
            JButton eyeBtn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    AppTheme.applyQualityRendering(g2);
                    Color eyeColor = showPassword ? AppTheme.ACCENT_AMBER : AppTheme.TEXT_MUTED;
                    drawEyeIcon(g2, getWidth(), getHeight(), eyeColor, showPassword);
                    g2.dispose();
                }
            };
            eyeBtn.setFocusPainted(false);
            eyeBtn.setFocusable(false); // keep Tab order on the inputs
            eyeBtn.setBorderPainted(false);
            eyeBtn.setContentAreaFilled(false);
            eyeBtn.setOpaque(false);
            eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            eyeBtn.setPreferredSize(new Dimension(22, 22));
            eyeBtn.setToolTipText("Toggle password visibility");
            eyeBtn.addActionListener(e -> {
                showPassword = !showPassword;
                passwordField.setEchoChar(showPassword ? (char) 0 : '•');
                eyeBtn.repaint();
                AudioService.playClick();
            });
            inputBox.add(eyeBtn, BorderLayout.EAST);

        } else {
            textField = new JTextField(initialValue);
            textField.setFont(AppTheme.FONT_BODY);
            textField.setForeground(AppTheme.TEXT_PRIMARY);
            textField.setCaretColor(AppTheme.ACCENT_AMBER);
            textField.setOpaque(false);
            textField.setBorder(null);
            textField.addFocusListener(focusListener);
            inputBox.add(textField, BorderLayout.CENTER);
        }

        add(inputBox, BorderLayout.CENTER);
    }

    public String getText() {
        if (isPassword) {
            return new String(passwordField.getPassword());
        }
        return textField.getText();
    }

    public void setText(String text) {
        if (isPassword) {
            passwordField.setText(text);
        } else {
            textField.setText(text);
        }
    }

    public void addActionListener(ActionListener l) {
        if (isPassword) {
            passwordField.addActionListener(l);
        } else {
            textField.addActionListener(l);
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        if (isPassword) {
            return passwordField.requestFocusInWindow();
        }
        return textField.requestFocusInWindow();
    }

    public void selectAll() {
        if (isPassword) {
            passwordField.selectAll();
        } else {
            textField.selectAll();
        }
    }

    private void drawLeadingIcon(Graphics2D g2, IconType type, int w, int h, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = w / 2;
        int cy = h / 2;

        switch (type) {
            case USER:
                // Head
                g2.drawOval(cx - 4, cy - 8, 8, 8);
                // Shoulders
                g2.drawArc(cx - 8, cy + 2, 16, 12, 0, 180);
                break;

            case LOCK:
                // Body
                g2.drawRoundRect(cx - 6, cy - 2, 12, 10, 2, 2);
                // Shackle
                g2.drawArc(cx - 4, cy - 8, 8, 9, 0, 180);
                // Keyhole
                g2.fillOval(cx - 1, cy + 2, 2, 2);
                g2.drawLine(cx, cy + 3, cx, cy + 6);
                break;

            case BADGE:
                // ID Card
                g2.drawRoundRect(cx - 7, cy - 6, 14, 12, 2, 2);
                g2.drawOval(cx - 3, cy - 4, 3, 3);
                g2.drawLine(cx + 2, cy - 3, cx + 5, cy - 3);
                g2.drawLine(cx - 4, cy + 2, cx + 5, cy + 2);
                break;
        }
    }

    private void drawEyeIcon(Graphics2D g2, int w, int h, Color color, boolean open) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = w / 2;
        int cy = h / 2;

        // Eye shape
        g2.drawArc(cx - 8, cy - 5, 16, 10, 0, 180);
        g2.drawArc(cx - 8, cy - 5, 16, 10, 180, 180);
        // Pupil
        g2.fillOval(cx - 2, cy - 2, 4, 4);

        if (!open) {
            // Strikethrough diagonal line
            g2.drawLine(cx - 7, cy - 6, cx + 7, cy + 6);
        }
    }
}
