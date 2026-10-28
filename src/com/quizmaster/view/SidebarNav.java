package com.quizmaster.view;

import com.quizmaster.service.AudioService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.function.Consumer;

/**
 * Left sidebar navigation rail (width: 72px) with vector icons,
 * active indicator pill, and screen switching callbacks.
 */
public class SidebarNav extends JPanel {

    public static final String NAV_HOME = "HOME";
    public static final String NAV_QUIZ = "QUIZ";
    public static final String NAV_HISTORY = "HISTORY";
    public static final String NAV_SETTINGS = "SETTINGS";
    public static final String NAV_LOGOUT = "LOGOUT";

    private String activeNav = NAV_HOME;
    private final Consumer<String> onNavigate;

    public SidebarNav(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, AppTheme.WINDOW_HEIGHT));
        setBackground(AppTheme.BG_SURFACE_DARK);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(20));

        // Top App Icon / Brand
        add(createBrandBadge());
        add(Box.createVerticalStrut(36));

        // Nav Buttons
        add(createNavButton(NAV_HOME, "Home", NavIcon.HOME));
        add(Box.createVerticalStrut(16));
        add(createNavButton(NAV_QUIZ, "Quizzes", NavIcon.QUIZ));
        add(Box.createVerticalStrut(16));
        add(createNavButton(NAV_HISTORY, "History", NavIcon.HISTORY));
        add(Box.createVerticalGlue());

        // Settings and Logout at bottom
        add(createNavButton(NAV_SETTINGS, "Settings", NavIcon.SETTINGS));
        add(Box.createVerticalStrut(12));
        add(createNavButton(NAV_LOGOUT, "Log Out", NavIcon.LOGOUT));
        add(Box.createVerticalStrut(20));
    }

    public void setActiveNav(String nav) {
        this.activeNav = nav;
        repaint();
    }

    public String getActiveNav() {
        return activeNav;
    }

    private JComponent createBrandBadge() {
        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);

                int size = 42;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Gradient rounded emblem
                GradientPaint gp = new GradientPaint(
                        x, y, AppTheme.ACCENT_AMBER,
                        x + size, y + size, new Color(0xD9, 0x77, 0x06)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, size, size, 12, 12);

                // 'Q' lettermark
                g2.setColor(AppTheme.TEXT_DARK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (size - fm.stringWidth("Q")) / 2;
                int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString("Q", tx, ty);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(72, 48);
            }
        };
        badge.setOpaque(false);
        return badge;
    }

    private JComponent createNavButton(String id, String tooltip, NavIcon icon) {
        JPanel btn = new JPanel() {
            private boolean isHovered = false;

            {
                setOpaque(false);
                setToolTipText(tooltip);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        AudioService.playClick();
                        // BUG-18: Logout is an action, not a destination. Keeping the
                        // previous nav active avoids a misleading amber highlight when
                        // the user cancels the sign-out confirmation dialog.
                        if (!NAV_LOGOUT.equals(id)) {
                            activeNav = id;
                        }
                        repaint();
                        SidebarNav.this.repaint();
                        if (onNavigate != null) {
                            onNavigate.accept(id);
                        }
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);

                boolean isActive = activeNav.equals(id);
                int w = getWidth();
                int h = getHeight();

                int btnW = 48;
                int btnH = 48;
                int bx = (w - btnW) / 2;
                int by = (h - btnH) / 2;

                if (isActive) {
                    // Amber rounded active background pill
                    g2.setColor(AppTheme.withAlpha(AppTheme.ACCENT_AMBER, 40));
                    g2.fillRoundRect(bx, by, btnW, btnH, 14, 14);

                    g2.setColor(AppTheme.ACCENT_AMBER);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(bx, by, btnW, btnH, 14, 14);

                    // Left vertical accent indicator bar
                    g2.fillRoundRect(2, by + 10, 4, btnH - 20, 2, 2);
                } else if (isHovered) {
                    g2.setColor(AppTheme.withAlpha(AppTheme.BG_CARD_HOVER, 180));
                    g2.fillRoundRect(bx, by, btnW, btnH, 14, 14);
                }

                Color iconColor = isActive ? AppTheme.ACCENT_AMBER :
                                  isHovered ? AppTheme.TEXT_PRIMARY : AppTheme.TEXT_MUTED;
                drawIcon(g2, icon, bx + btnW / 2, by + btnH / 2, iconColor);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(72, 54);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(72, 54);
            }
        };

        return btn;
    }

    private void drawIcon(Graphics2D g2, NavIcon icon, int cx, int cy, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (icon) {
            case HOME:
                // House shape
                GeneralPath roof = new GeneralPath();
                roof.moveTo(cx - 9, cy + 1);
                roof.lineTo(cx, cy - 8);
                roof.lineTo(cx + 9, cy + 1);
                g2.draw(roof);
                g2.drawRect(cx - 7, cy + 1, 14, 8);
                g2.drawLine(cx, cy + 9, cx, cy + 5);
                break;

            case QUIZ:
                // Checklist / notebook shape
                g2.drawRoundRect(cx - 8, cy - 9, 16, 18, 3, 3);
                g2.drawLine(cx - 4, cy - 4, cx + 4, cy - 4);
                g2.drawLine(cx - 4, cy, cx + 4, cy);
                g2.drawLine(cx - 4, cy + 4, cx + 1, cy + 4);
                break;

            case HISTORY:
                // Clock / history circular icon
                g2.drawOval(cx - 9, cy - 9, 18, 18);
                g2.drawLine(cx, cy - 5, cx, cy);
                g2.drawLine(cx, cy, cx + 4, cy + 2);
                break;

            case SETTINGS:
                // Cog wheel
                g2.drawOval(cx - 5, cy - 5, 10, 10);
                for (int i = 0; i < 4; i++) {
                    double angle = Math.toRadians(i * 45);
                    int dx = (int) (Math.cos(angle) * 8);
                    int dy = (int) (Math.sin(angle) * 8);
                    g2.drawLine(cx - dx, cy - dy, cx + dx, cy + dy);
                }
                g2.setColor(AppTheme.BG_SURFACE_DARK);
                g2.fillOval(cx - 4, cy - 4, 8, 8);
                g2.setColor(color);
                g2.drawOval(cx - 4, cy - 4, 8, 8);
                break;

            case LOGOUT:
                // Door frame + exit arrow
                g2.drawRect(cx - 8, cy - 8, 9, 16);
                g2.drawLine(cx - 2, cy, cx + 8, cy);
                g2.drawLine(cx + 4, cy - 4, cx + 8, cy);
                g2.drawLine(cx + 4, cy + 4, cx + 8, cy);
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw right subtle hairline border
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(AppTheme.BORDER_SUBTLE);
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        g2.dispose();
    }

    private enum NavIcon {
        HOME, QUIZ, HISTORY, SETTINGS, LOGOUT
    }
}
