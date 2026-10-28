package com.quizmaster.view;

import com.quizmaster.components.AuthInputField;
import com.quizmaster.components.PrimaryButton;
import com.quizmaster.components.RoundedCard;
import com.quizmaster.components.SecondaryButton;
import com.quizmaster.service.AudioService;
import com.quizmaster.service.StorageService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Screen: Authentication (Login & Account Registration).
 * Designed with modern, human-grade UI/UX:
 * - Ambient radial background glow
 * - Custom brand emblem with feature badges
 * - Sleek segmented tab control
 * - Leading vector icons & focus glow on inputs
 * - Password visibility eye toggle
 * - Dismissible warning alert pill
 * - One-click demo autofill
 */
public class LoginPanel extends JPanel {

    private final StorageService storageService;
    private final Consumer<String> onLoginSuccess;

    private boolean isRegisterMode = false;

    // Form inputs with leading icons and focus glow
    private AuthInputField signInUserField;
    private AuthInputField signInPassField;

    private AuthInputField regNameField;
    private AuthInputField regUserField;
    private AuthInputField regPassField;
    private AuthInputField regConfirmPassField;

    // Layout & Components
    private RoundedCard card;
    private JPanel formHostPanel;
    private JPanel signInPanel;
    private JPanel registerPanel;
    private JPanel tabSegmentedPanel;
    private JPanel errorBannerPanel;
    private JLabel errorBannerLabel;
    private PrimaryButton submitBtn;
    private JLabel demoHintLabel;

    public LoginPanel(StorageService storageService, Consumer<String> onLoginSuccess) {
        this.storageService = storageService;
        this.onLoginSuccess = onLoginSuccess;

        setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());

        initUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        AppTheme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // 1. Subtle ambient radial amber glow behind the card
        float radius = Math.max(w, h) * 0.45f;
        Point center = new Point(w / 2, h / 2);
        float[] dist = {0.0f, 0.45f, 1.0f};
        Color[] colors = {
                AppTheme.withAlpha(AppTheme.ACCENT_AMBER, 22),
                AppTheme.withAlpha(new Color(0x38, 0xBD, 0xF8), 10),
                new Color(0x0F, 0x17, 0x2A, 0)
        };
        RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
        g2.setPaint(rgp);
        g2.fillRect(0, 0, w, h);

        // 2. Subtle modern grid accent lines (5% alpha)
        g2.setColor(new Color(255, 255, 255, 12));
        g2.setStroke(new BasicStroke(1f));
        int gridSpacing = 48;
        for (int x = 0; x < w; x += gridSpacing) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += gridSpacing) {
            g2.drawLine(0, y, w, y);
        }

        g2.dispose();
    }

    private void initUI() {
        // Main Centered Card Container (auto-calculated dynamic height)
        card = new RoundedCard(20, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(440, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 30, 20, 30));

        // ── 1. Top Brand Header ────────────────────────────────────────
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Vector Emblem
        JPanel emblem = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);

                int size = 46;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Glowing outer aura
                g2.setColor(AppTheme.withAlpha(AppTheme.ACCENT_AMBER, 35));
                g2.fillOval(x - 5, y - 5, size + 10, size + 10);

                // Core rounded amber gradient
                GradientPaint gp = new GradientPaint(
                        x, y, AppTheme.ACCENT_AMBER,
                        x + size, y + size, new Color(0xD9, 0x77, 0x06)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, size, size, 14, 14);

                // Lettermark 'Q'
                g2.setColor(AppTheme.TEXT_DARK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (size - fm.stringWidth("Q")) / 2;
                int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString("Q", tx, ty);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() { return new Dimension(54, 54); }
            @Override
            public Dimension getMinimumSize() { return new Dimension(54, 54); }
            @Override
            public Dimension getMaximumSize() { return new Dimension(54, 54); }
        };
        emblem.setOpaque(false);
        emblem.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(emblem);
        headerPanel.add(Box.createVerticalStrut(8));

        JLabel titleLabel = new JLabel("QuizMaster Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Java Knowledge & Skill Assessment");
        subtitleLabel.setFont(AppTheme.FONT_SMALL);
        subtitleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        // Feature Pill Tags with crisp colored vector dots (no emojis)
        JPanel featurePills = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        featurePills.setOpaque(false);
        featurePills.setAlignmentX(Component.CENTER_ALIGNMENT);
        featurePills.add(createFeatureTag("Java 17+", AppTheme.ACCENT_BLUE));
        featurePills.add(createFeatureTag("Live Scoring", AppTheme.ACCENT_GREEN));
        featurePills.add(createFeatureTag("Streaks", AppTheme.ACCENT_AMBER));
        headerPanel.add(featurePills);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(14));

        // ── 2. Segmented Tab Switcher (Sign In | Create Account) ───────
        tabSegmentedPanel = createSegmentedTabControl();
        card.add(tabSegmentedPanel);
        card.add(Box.createVerticalStrut(12));

        // ── 3. Error Alert Banner ──────────────────────────────────────
        errorBannerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                int w = getWidth();
                int h = getHeight();

                g2.setColor(AppTheme.withAlpha(AppTheme.ACCENT_RED, 30));
                g2.fillRoundRect(0, 0, w, h, 8, 8);
                g2.setColor(AppTheme.withAlpha(AppTheme.ACCENT_RED, 150));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                g2.dispose();
            }
        };
        errorBannerPanel.setOpaque(false);
        errorBannerPanel.setPreferredSize(new Dimension(380, 28));
        errorBannerPanel.setMaximumSize(new Dimension(380, 28));
        errorBannerPanel.setMinimumSize(new Dimension(380, 28));
        errorBannerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorBannerLabel = new JLabel("");
        errorBannerLabel.setFont(AppTheme.FONT_SMALL_BOLD);
        errorBannerLabel.setForeground(new Color(0xFE, 0x81, 0x81));
        errorBannerPanel.add(errorBannerLabel);
        errorBannerPanel.setVisible(false);

        card.add(errorBannerPanel);
        card.add(Box.createVerticalStrut(8));

        // ── 4. Forms Container (Swapped dynamically to avoid ghost space) ─
        formHostPanel = new JPanel(new BorderLayout());
        formHostPanel.setOpaque(false);
        formHostPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // A. Sign In Panel
        signInPanel = new JPanel();
        signInPanel.setLayout(new BoxLayout(signInPanel, BoxLayout.Y_AXIS));
        signInPanel.setOpaque(false);

        // UX-12: start empty — demo credentials come from the quick-fill chip below
        signInUserField = new AuthInputField("Username", AuthInputField.IconType.USER, false, "");
        signInPassField = new AuthInputField("Password", AuthInputField.IconType.LOCK, true, "");

        signInPanel.add(signInUserField);
        signInPanel.add(Box.createVerticalStrut(8));
        signInPanel.add(signInPassField);
        signInPanel.add(Box.createVerticalStrut(6));

        // Quick Autofill Chip
        JPanel quickFillRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        quickFillRow.setOpaque(false);
        quickFillRow.setPreferredSize(new Dimension(380, 22));
        quickFillRow.setMaximumSize(new Dimension(380, 22));

        JButton quickFillBtn = new JButton("Quick-fill: Taha (Demo) →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                g2.setFont(AppTheme.FONT_SMALL);
                g2.setColor(AppTheme.ACCENT_AMBER);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 0, fm.getAscent());
                g2.dispose();
            }
        };
        quickFillBtn.setFocusPainted(false);
        quickFillBtn.setBorderPainted(false);
        quickFillBtn.setContentAreaFilled(false);
        quickFillBtn.setOpaque(false);
        quickFillBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        quickFillBtn.setPreferredSize(new Dimension(170, 20));
        quickFillBtn.addActionListener(e -> {
            signInUserField.setText("taha");
            signInPassField.setText("taha123");
            AudioService.playClick();
        });
        quickFillRow.add(quickFillBtn);
        signInPanel.add(quickFillRow);

        // B. Register Panel
        registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setOpaque(false);

        regNameField = new AuthInputField("Full Name", AuthInputField.IconType.BADGE, false, "");
        regUserField = new AuthInputField("Username", AuthInputField.IconType.USER, false, "");
        regPassField = new AuthInputField("Password", AuthInputField.IconType.LOCK, true, "");
        regConfirmPassField = new AuthInputField("Confirm Password", AuthInputField.IconType.LOCK, true, "");

        registerPanel.add(regNameField);
        registerPanel.add(Box.createVerticalStrut(8));
        registerPanel.add(regUserField);
        registerPanel.add(Box.createVerticalStrut(8));
        registerPanel.add(regPassField);
        registerPanel.add(Box.createVerticalStrut(8));
        registerPanel.add(regConfirmPassField);

        formHostPanel.add(signInPanel, BorderLayout.CENTER);

        card.add(formHostPanel);
        card.add(Box.createVerticalStrut(14));

        // ── 5. Action Buttons (Sign In CTA + Guest + Demo Hint) ─────────
        submitBtn = new PrimaryButton("Sign In →");
        submitBtn.setPreferredSize(new Dimension(380, 44));
        submitBtn.setMaximumSize(new Dimension(380, 44));
        submitBtn.setMinimumSize(new Dimension(380, 44));
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn.addActionListener(e -> handleSubmit());
        card.add(submitBtn);

        card.add(Box.createVerticalStrut(8));

        SecondaryButton guestBtn = new SecondaryButton("Continue as Guest →");
        guestBtn.setPreferredSize(new Dimension(380, 36));
        guestBtn.setMaximumSize(new Dimension(380, 36));
        guestBtn.setMinimumSize(new Dimension(380, 36));
        guestBtn.setFont(AppTheme.FONT_BODY_BOLD);
        guestBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        guestBtn.addActionListener(e -> handleGuestLogin());
        card.add(guestBtn);

        card.add(Box.createVerticalStrut(12));

        demoHintLabel = new JLabel("Default Demo: taha  |  Password: taha123");
        demoHintLabel.setFont(AppTheme.FONT_SMALL);
        demoHintLabel.setForeground(AppTheme.ACCENT_AMBER_DIM);
        demoHintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(demoHintLabel);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(16, 16, 16, 16));
        centerWrapper.add(card);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Bind Enter key to submit on all inputs
        ActionListener submitListener = e -> handleSubmit();
        signInUserField.addActionListener(submitListener);
        signInPassField.addActionListener(submitListener);
        regNameField.addActionListener(submitListener);
        regUserField.addActionListener(submitListener);
        regPassField.addActionListener(submitListener);
        regConfirmPassField.addActionListener(submitListener);
    }

    private JPanel createSegmentedTabControl() {
        JPanel segment = new JPanel(new GridLayout(1, 2, 4, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                int w = getWidth();
                int h = getHeight();
                int r = 12;

                g2.setColor(new Color(0x0E, 0x16, 0x26));
                g2.fillRoundRect(0, 0, w, h, r, r);
                g2.setColor(AppTheme.BORDER_SUBTLE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() { return new Dimension(380, 42); }
            @Override
            public Dimension getMinimumSize() { return new Dimension(380, 42); }
            @Override
            public Dimension getMaximumSize() { return new Dimension(380, 42); }
        };
        segment.setOpaque(false);
        segment.setBorder(new EmptyBorder(3, 3, 3, 3));
        segment.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton signInTab = createSegmentTabButton("Sign In", true);
        JButton registerTab = createSegmentTabButton("Create Account", false);

        signInTab.addActionListener(e -> setRegisterMode(false));
        registerTab.addActionListener(e -> setRegisterMode(true));

        segment.add(signInTab);
        segment.add(registerTab);
        return segment;
    }

    private JButton createSegmentTabButton(String text, boolean initialActive) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                int w = getWidth();
                int h = getHeight();

                boolean isCurrentActive = isRegisterMode ? text.contains("Account") : text.contains("Sign");

                if (isCurrentActive) {
                    // Active Amber Pill
                    g2.setColor(AppTheme.ACCENT_AMBER);
                    g2.fillRoundRect(0, 0, w, h, 10, 10);
                    g2.setColor(AppTheme.TEXT_DARK);
                    g2.setFont(AppTheme.FONT_SUBHEADING);
                } else {
                    g2.setColor(AppTheme.TEXT_SECONDARY);
                    g2.setFont(AppTheme.FONT_BODY);
                }

                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createFeatureTag(String text, Color dotColor) {
        JPanel tag = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(0x13, 0x1E, 0x30));
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                g2.setColor(AppTheme.withAlpha(AppTheme.BORDER_SUBTLE, 120));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g2.dispose();
            }
        };
        tag.setOpaque(false);
        tag.setBorder(new EmptyBorder(1, 8, 1, 8));

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                g2.setColor(dotColor);
                g2.fillOval(0, 4, 6, 6);
                g2.dispose();
            }
            @Override
            public Dimension getPreferredSize() { return new Dimension(6, 14); }
        };
        dot.setOpaque(false);
        tag.add(dot);

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(AppTheme.TEXT_SECONDARY);
        tag.add(label);

        return tag;
    }

    public void setRegisterMode(boolean register) {
        this.isRegisterMode = register;
        hideError();
        AudioService.playClick();

        formHostPanel.removeAll();
        if (register) {
            formHostPanel.add(registerPanel, BorderLayout.CENTER);
            submitBtn.setText("Create Account →");
            demoHintLabel.setVisible(false);
            regNameField.requestFocusInWindow();
        } else {
            formHostPanel.add(signInPanel, BorderLayout.CENTER);
            submitBtn.setText("Sign In →");
            demoHintLabel.setVisible(true);
            signInUserField.requestFocusInWindow();
        }

        formHostPanel.revalidate();
        formHostPanel.repaint();
        tabSegmentedPanel.repaint();
        card.revalidate();
        card.repaint();
        revalidate();
        repaint();
    }

    private void handleSubmit() {
        hideError();

        if (isRegisterMode) {
            String name = regNameField.getText().trim();
            String user = regUserField.getText().trim();
            String pass = regPassField.getText().trim();
            String confirm = regConfirmPassField.getText().trim();

            if (user.isEmpty()) {
                showError("Please choose a username.");
                regUserField.requestFocusInWindow();
                return;
            }

            if (pass.isEmpty()) {
                showError("Please enter a password.");
                regPassField.requestFocusInWindow();
                return;
            }

            if (pass.length() < 4) {
                showError("Password must be at least 4 characters.");
                regPassField.requestFocusInWindow();
                return;
            }

            if (!pass.equals(confirm)) {
                showError("Passwords do not match.");
                regConfirmPassField.requestFocusInWindow();
                return;
            }

            if (storageService.userExists(user)) {
                showError("Username '" + user + "' is already taken.");
                regUserField.requestFocusInWindow();
                return;
            }

            boolean ok = storageService.register(user, name, pass);
            if (ok) {
                AudioService.playCorrect();
                if (onLoginSuccess != null) {
                    onLoginSuccess.accept(user);
                }
            } else {
                showError("Could not create account. Please try again.");
            }

        } else {
            // Sign in mode
            String user = signInUserField.getText().trim();
            String pass = signInPassField.getText().trim();

            if (user.isEmpty()) {
                showError("Please enter your username.");
                signInUserField.requestFocusInWindow();
                return;
            }

            if (pass.isEmpty()) {
                showError("Please enter your password.");
                signInPassField.requestFocusInWindow();
                return;
            }

            boolean ok = storageService.authenticate(user, pass);
            if (ok) {
                AudioService.playCorrect();
                if (onLoginSuccess != null) {
                    onLoginSuccess.accept(user);
                }
            } else {
                AudioService.playWrong();
                showError("Invalid username or password.");
                signInPassField.selectAll();
                signInPassField.requestFocusInWindow();
            }
        }
    }

    private void handleGuestLogin() {
        hideError();
        final String guestName = "guest";
        final String guestPass = "guest123";

        boolean authenticated;
        if (!storageService.userExists(guestName)) {
            authenticated = storageService.register(guestName, "Guest Student", guestPass);
        } else {
            // BUG-05: the result of authenticate() must be honoured, otherwise the
            // app continues with a null currentAccount and reads the wrong profile.
            authenticated = storageService.authenticate(guestName, guestPass);
        }

        if (!authenticated || storageService.getCurrentAccount() == null) {
            AudioService.playWrong();
            showError("Guest sign-in failed. Please sign in with your own account.");
            return;
        }

        AudioService.playClick();
        if (onLoginSuccess != null) {
            onLoginSuccess.accept(guestName);
        }
    }

    private void showError(String msg) {
        errorBannerLabel.setText(msg);
        errorBannerPanel.setVisible(true);
        AudioService.playWrong();
        revalidate();
        repaint();
    }

    private void hideError() {
        errorBannerPanel.setVisible(false);
        errorBannerLabel.setText("");
        revalidate();
        repaint();
    }

    public void resetForm() {
        setRegisterMode(false);
        // BUG-14 / UX-12: never restore credentials after a logout — the demo hint
        // label and the quick-fill chip already cover the demo flow.
        signInUserField.setText("");
        signInPassField.setText("");
        regNameField.setText("");
        regUserField.setText("");
        regPassField.setText("");
        regConfirmPassField.setText("");
        hideError();
    }
}
