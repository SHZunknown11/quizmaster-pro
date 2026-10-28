package com.quizmaster.view;

import com.quizmaster.components.PrimaryButton;
import com.quizmaster.components.RoundedCard;
import com.quizmaster.components.SecondaryButton;
import com.quizmaster.model.UserProfile;
import com.quizmaster.service.AudioService;
import com.quizmaster.service.StorageService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Screen: Settings Panel.
 * Configures audio preferences, timer duration, user profile name, and reset options.
 */
public class SettingsPanel extends JPanel {

    private final StorageService storageService;
    private final Runnable onBackToHome;
    private final Runnable onProfileUpdated;
    private final Runnable onLogout;

    private JTextField nameField;
    private JCheckBox soundCheckBox;
    private JComboBox<Integer> timerCombo;
    private int defaultTimer = UserProfile.DEFAULT_TIMER_SECONDS;

    public SettingsPanel(StorageService storageService, Runnable onBackToHome, Runnable onProfileUpdated, Runnable onLogout) {
        this.storageService = storageService;
        this.onBackToHome = onBackToHome;
        this.onProfileUpdated = onProfileUpdated;
        this.onLogout = onLogout;

        setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 36, 28, 36));

        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setOpaque(false);

        JLabel title = new JLabel("Settings & Preferences");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titles.add(title);

        JLabel subtitle = new JLabel("Manage your profile, sound cues, and quiz preferences");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        titles.add(subtitle);

        header.add(titles, BorderLayout.WEST);

        SecondaryButton backBtn = new SecondaryButton("← Dashboard");
        backBtn.setPreferredSize(new Dimension(130, 38));
        backBtn.addActionListener(e -> {
            if (onBackToHome != null) onBackToHome.run();
        });
        header.add(backBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center: Settings sections
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(24, 0, 0, 0));

        // 1. Profile Settings Card
        RoundedCard profileCard = new RoundedCard(12, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
        profileCard.setLayout(new BorderLayout());
        profileCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel profLeft = new JPanel(new GridLayout(2, 1, 0, 4));
        profLeft.setOpaque(false);
        JLabel profTitle = new JLabel("Display Name");
        profTitle.setFont(AppTheme.FONT_BODY_BOLD);
        profTitle.setForeground(AppTheme.TEXT_PRIMARY);
        profLeft.add(profTitle);

        JLabel profSub = new JLabel("Personalizes your greeting on the Dashboard");
        profSub.setFont(AppTheme.FONT_SMALL);
        profSub.setForeground(AppTheme.TEXT_MUTED);
        profLeft.add(profSub);
        profileCard.add(profLeft, BorderLayout.WEST);

        JPanel profRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        profRight.setOpaque(false);

        UserProfile profile = storageService.loadProfile();
        nameField = new JTextField(profile.getDisplayName(), 12);
        nameField.setFont(AppTheme.FONT_BODY);
        nameField.setBackground(AppTheme.BG_SURFACE_DARK);
        nameField.setForeground(AppTheme.TEXT_PRIMARY);
        nameField.setCaretColor(AppTheme.ACCENT_AMBER);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        profRight.add(nameField);

        PrimaryButton saveNameBtn = new PrimaryButton("Save");
        saveNameBtn.setPreferredSize(new Dimension(80, 36));
        saveNameBtn.addActionListener(e -> {
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                UserProfile p = storageService.loadProfile();
                p.setDisplayName(newName);
                storageService.saveProfile(p);
                AudioService.playCorrect();
                JOptionPane.showMessageDialog(this, "Profile name updated to: " + newName, "Success", JOptionPane.INFORMATION_MESSAGE);
                if (onProfileUpdated != null) onProfileUpdated.run();
            }
        });
        profRight.add(saveNameBtn);

        SecondaryButton logoutBtn = new SecondaryButton("Sign Out");
        logoutBtn.setPreferredSize(new Dimension(90, 36));
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) onLogout.run();
        });
        profRight.add(logoutBtn);

        profileCard.add(profRight, BorderLayout.EAST);

        center.add(profileCard);
        center.add(Box.createVerticalStrut(14));

        // 2. Audio & Timer Card
        RoundedCard audioCard = new RoundedCard(12, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
        audioCard.setLayout(new GridLayout(2, 2, 16, 12));
        audioCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        audioCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel soundLabel = new JLabel("UI Audio Effects (Procedural Sound)");
        soundLabel.setFont(AppTheme.FONT_BODY_BOLD);
        soundLabel.setForeground(AppTheme.TEXT_PRIMARY);
        audioCard.add(soundLabel);

        soundCheckBox = new JCheckBox("Sound Enabled", AudioService.isSoundEnabled());
        soundCheckBox.setFont(AppTheme.FONT_BODY);
        soundCheckBox.setForeground(AppTheme.ACCENT_AMBER);
        soundCheckBox.setOpaque(false);
        soundCheckBox.setFocusPainted(false);
        soundCheckBox.addActionListener(e -> {
            AudioService.setSoundEnabled(soundCheckBox.isSelected());
            if (soundCheckBox.isSelected()) {
                AudioService.playClick();
            }
        });
        audioCard.add(soundCheckBox);

        JLabel timerLabel = new JLabel("Question Timer Duration");
        timerLabel.setFont(AppTheme.FONT_BODY_BOLD);
        timerLabel.setForeground(AppTheme.TEXT_PRIMARY);
        audioCard.add(timerLabel);

        Integer[] timerOptions = {15, 30, 45, 60};
        timerCombo = new JComboBox<>(timerOptions);
        // UX-05: start from the persisted preference, not a hardcoded 30s
        defaultTimer = profile.getTimerPreference();
        timerCombo.setSelectedItem(defaultTimer);
        timerCombo.setFont(AppTheme.FONT_BODY);
        timerCombo.setBackground(AppTheme.BG_SURFACE_DARK);
        timerCombo.setForeground(AppTheme.TEXT_PRIMARY);
        timerCombo.addActionListener(e -> {
            Integer selected = (Integer) timerCombo.getSelectedItem();
            if (selected == null) return;
            defaultTimer = selected;
            persistTimerPreference(defaultTimer);
        });
        audioCard.add(timerCombo);

        center.add(audioCard);
        center.add(Box.createVerticalStrut(14));

        // 3. System & Reset Card
        RoundedCard systemCard = new RoundedCard(12, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
        systemCard.setLayout(new BorderLayout());
        systemCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        systemCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel sysLeft = new JPanel(new GridLayout(2, 1, 0, 4));
        sysLeft.setOpaque(false);
        JLabel sysTitle = new JLabel("Reset Data & Statistics");
        sysTitle.setFont(AppTheme.FONT_BODY_BOLD);
        sysTitle.setForeground(AppTheme.ACCENT_RED);
        sysLeft.add(sysTitle);

        JLabel sysSub = new JLabel("Reset quizzes count, best score, streak, and quiz history");
        sysSub.setFont(AppTheme.FONT_SMALL);
        sysSub.setForeground(AppTheme.TEXT_MUTED);
        sysLeft.add(sysSub);
        systemCard.add(sysLeft, BorderLayout.WEST);

        SecondaryButton resetBtn = new SecondaryButton("Reset All Data");
        resetBtn.setPreferredSize(new Dimension(140, 36));
        resetBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to reset all quiz statistics and history?",
                    "Confirm Data Reset",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                // BUG-04: never hardcode the username — keep the account that is
                // actually signed in, otherwise the profile lookup is corrupted.
                UserProfile current = storageService.loadProfile();
                UserProfile fresh = new UserProfile();
                fresh.setUsername(resolveCurrentUsername(current));
                String typedName = nameField.getText().trim();
                fresh.setDisplayName(typedName.isEmpty() ? current.getDisplayName() : typedName);
                fresh.setTotalQuizzes(0);
                fresh.setBestScore(0);
                fresh.setAverageScore(0);
                fresh.setDayStreak(0);
                fresh.setLastQuizDate("");
                fresh.setTimerPreference(current.getTimerPreference());
                storageService.saveProfile(fresh);
                AudioService.playWrong();
                JOptionPane.showMessageDialog(this, "All statistics have been reset.", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
                if (onProfileUpdated != null) onProfileUpdated.run();
            }
        });
        systemCard.add(resetBtn, BorderLayout.EAST);

        center.add(systemCard);
        center.add(Box.createVerticalStrut(14));

        // 4. About Info
        RoundedCard aboutCard = new RoundedCard(12, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
        aboutCard.setLayout(new GridLayout(3, 1, 0, 4));
        aboutCard.setBorder(new EmptyBorder(14, 18, 14, 18));
        aboutCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel aboutTitle = new JLabel("QuizMaster Pro — Semester 5 Java Desktop Project");
        aboutTitle.setFont(AppTheme.FONT_BODY_BOLD);
        aboutTitle.setForeground(AppTheme.TEXT_PRIMARY);
        aboutCard.add(aboutTitle);

        JLabel aboutVer = new JLabel("Java Swing + Graphics2D Vector UI · Zero-Dependency JSON Storage");
        aboutVer.setFont(AppTheme.FONT_SMALL);
        aboutVer.setForeground(AppTheme.ACCENT_AMBER);
        aboutCard.add(aboutVer);

        JLabel aboutRuntime = new JLabel("JDK Runtime: " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        aboutRuntime.setFont(AppTheme.FONT_SMALL);
        aboutRuntime.setForeground(AppTheme.TEXT_MUTED);
        aboutCard.add(aboutRuntime);

        center.add(aboutCard);

        add(center, BorderLayout.CENTER);
    }

    public int getDefaultTimer() {
        return defaultTimer;
    }

    /**
     * BUG-04: resolves the username of the account that is actually signed in,
     * falling back to the one stored in the profile file.
     */
    private String resolveCurrentUsername(UserProfile loadedProfile) {
        if (storageService.getCurrentAccount() != null) {
            return storageService.getCurrentAccount().getUsername();
        }
        return loadedProfile.getUsername();
    }

    /**
     * UX-05: writes the chosen timer duration to the user's profile JSON so it
     * survives an app restart.
     */
    private void persistTimerPreference(int seconds) {
        UserProfile p = storageService.loadProfile();
        if (p.getTimerPreference() == seconds) return;
        p.setTimerPreference(seconds);
        storageService.saveProfile(p);
    }

    public void refreshProfile() {
        UserProfile profile = storageService.loadProfile();
        if (nameField != null) {
            nameField.setText(profile.getDisplayName());
        }
        // UX-05: keep the dropdown in sync with the persisted preference
        defaultTimer = profile.getTimerPreference();
        if (timerCombo != null && !Integer.valueOf(defaultTimer).equals(timerCombo.getSelectedItem())) {
            timerCombo.setSelectedItem(defaultTimer);
        }
        if (soundCheckBox != null) {
            soundCheckBox.setSelected(AudioService.isSoundEnabled());
        }
    }
}
