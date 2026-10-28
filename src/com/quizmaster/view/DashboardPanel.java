package com.quizmaster.view;

import com.quizmaster.components.*;
import com.quizmaster.model.UserProfile;
import com.quizmaster.model.UserProfile.ActivityEntry;
import com.quizmaster.service.StorageService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Screen 1: Dashboard Panel (The Left Mockup).
 * Displays time-of-day greeting, 2x2 KPI metrics grid, recent activity stream,
 * and prominent "Start New Quiz →" primary action button.
 */
public class DashboardPanel extends JPanel {

    private final StorageService storageService;
    private final Runnable onStartQuiz;

    private JLabel greetingLabel;
    private StatCard totalQuizzesCard;
    private StatCard bestScoreCard;
    private StatCard dayStreakCard;
    private StatCard avgScoreCard;
    private JPanel activityListContainer;

    public DashboardPanel(StorageService storageService, Runnable onStartQuiz) {
        this.storageService = storageService;
        this.onStartQuiz = onStartQuiz;

        setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 36, 28, 36));

        initUI();
        refreshData();
    }

    private void initUI() {
        // Scrollable content wrapper for clean responsive behavior
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // ── 1. Header Section ──────────────────────────────────────────
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        greetingLabel = new JLabel("Good morning, Taha");
        greetingLabel.setFont(AppTheme.FONT_TITLE);
        greetingLabel.setForeground(AppTheme.TEXT_PRIMARY);
        headerPanel.add(greetingLabel);

        JLabel subtitleLabel = new JLabel("Ready for today's challenge?");
        subtitleLabel.setFont(AppTheme.FONT_BODY);
        subtitleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        headerPanel.add(subtitleLabel);

        content.add(headerPanel);
        content.add(Box.createVerticalStrut(24));

        // ── 2. 2x2 Stats Grid ──────────────────────────────────────────
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        statsGrid.setOpaque(false);
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        totalQuizzesCard = new StatCard("Total Quizzes", "14", AppTheme.TEXT_PRIMARY, StatCard.IconType.NONE);
        bestScoreCard = new StatCard("Best Score", "92%", AppTheme.ACCENT_AMBER, StatCard.IconType.NONE);
        dayStreakCard = new StatCard("Day Streak", "5", AppTheme.TEXT_PRIMARY, StatCard.IconType.FLAME);
        avgScoreCard = new StatCard("Average Score", "78%", AppTheme.ACCENT_BLUE, StatCard.IconType.NONE);

        statsGrid.add(totalQuizzesCard);
        statsGrid.add(bestScoreCard);
        statsGrid.add(dayStreakCard);
        statsGrid.add(avgScoreCard);

        content.add(statsGrid);
        content.add(Box.createVerticalStrut(28));

        // ── 3. Recent Activity Section ─────────────────────────────────
        JLabel activityHeader = new JLabel("Recent Activity");
        activityHeader.setFont(AppTheme.FONT_HEADING);
        activityHeader.setForeground(AppTheme.ACCENT_AMBER);
        activityHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(activityHeader);
        content.add(Box.createVerticalStrut(14));

        activityListContainer = new JPanel();
        activityListContainer.setLayout(new BoxLayout(activityListContainer, BoxLayout.Y_AXIS));
        activityListContainer.setOpaque(false);
        activityListContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(activityListContainer);

        content.add(Box.createVerticalGlue());

        // Wrap in clean JScrollPane (smooth scrolling, hidden borders)
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // ── 4. Bottom Action Bar ───────────────────────────────────────
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(16, 0, 0, 0));

        PrimaryButton startBtn = new PrimaryButton("Start New Quiz →");
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        startBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 48));
        startBtn.addActionListener(e -> {
            if (onStartQuiz != null) {
                onStartQuiz.run();
            }
        });

        bottomBar.add(startBtn, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * Refreshes stats, greeting, and activity feed from disk.
     */
    public void refreshData() {
        UserProfile profile = storageService.loadProfile();

        String greeting = AppTheme.timeOfDayGreeting() + ", " + profile.getDisplayName();
        greetingLabel.setText(greeting);

        totalQuizzesCard.setValue(String.valueOf(profile.getTotalQuizzes()));
        bestScoreCard.setValue(profile.getBestScore() + "%");
        dayStreakCard.setValue(String.valueOf(profile.getDayStreak()));
        avgScoreCard.setValue(profile.getAverageScore() + "%");

        // Rebuild activity items
        activityListContainer.removeAll();
        List<ActivityEntry> list = profile.getRecentActivity();
        if (list.isEmpty()) {
            JLabel emptyLabel = new JLabel("No quizzes taken yet. Hit 'Start New Quiz' to begin!");
            emptyLabel.setFont(AppTheme.FONT_BODY);
            emptyLabel.setForeground(AppTheme.TEXT_MUTED);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            activityListContainer.add(emptyLabel);
        } else {
            for (ActivityEntry act : list) {
                activityListContainer.add(createActivityItem(act));
                activityListContainer.add(Box.createVerticalStrut(10));
            }
        }

        activityListContainer.revalidate();
        activityListContainer.repaint();
    }

    private JPanel createActivityItem(ActivityEntry act) {
        RoundedCard item = new RoundedCard(12, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
        item.setLayout(new BorderLayout());
        item.setBorder(new EmptyBorder(12, 16, 12, 16));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left: colored status dot + category name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        Color dotColor = act.getScorePercent() >= 80 ? AppTheme.ACCENT_GREEN :
                         act.getScorePercent() >= 60 ? AppTheme.ACCENT_AMBER : AppTheme.ACCENT_RED;

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                g2.setColor(dotColor);
                g2.fillOval(2, (getHeight() - 10) / 2, 10, 10);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(14, 18);
            }
        };
        dot.setOpaque(false);
        left.add(dot);

        JLabel title = new JLabel(act.getCategory());
        title.setFont(AppTheme.FONT_BODY_BOLD);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        left.add(title);

        item.add(left, BorderLayout.WEST);

        // Right: Score badge pill + relative date
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        PillBadge scorePill = new PillBadge(act.getScorePercent() + "%", dotColor);
        right.add(scorePill);

        JLabel dateLabel = new JLabel(act.getRelativeTime());
        dateLabel.setFont(AppTheme.FONT_SMALL);
        dateLabel.setForeground(AppTheme.TEXT_MUTED);
        right.add(dateLabel);

        item.add(right, BorderLayout.EAST);
        return item;
    }
}
