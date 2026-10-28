package com.quizmaster.view;

import com.quizmaster.components.PillBadge;
import com.quizmaster.components.RoundedCard;
import com.quizmaster.components.SecondaryButton;
import com.quizmaster.model.QuizResult;
import com.quizmaster.service.StorageService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Screen: Quiz History & Attempts.
 * Lists previous quiz attempts with score breakdown, duration, grade, and timestamp.
 */
public class HistoryPanel extends JPanel {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy · hh:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_ONLY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);

    private final StorageService storageService;
    private final Runnable onBackToHome;
    private JPanel historyListContainer;

    public HistoryPanel(StorageService storageService, Runnable onBackToHome) {
        this.storageService = storageService;
        this.onBackToHome = onBackToHome;

        setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 36, 28, 36));

        initUI();
    }

    private void initUI() {
        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setOpaque(false);

        JLabel title = new JLabel("Quiz History");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titles.add(title);

        JLabel subtitle = new JLabel("Review your past quiz performance and study progress");
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

        // Center: List of history items
        historyListContainer = new JPanel();
        historyListContainer.setLayout(new BoxLayout(historyListContainer, BoxLayout.Y_AXIS));
        historyListContainer.setOpaque(false);
        historyListContainer.setBorder(new EmptyBorder(20, 0, 0, 0));

        JScrollPane scroll = new JScrollPane(historyListContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);

        add(scroll, BorderLayout.CENTER);

        // BUG-13: populate immediately so the panel is never shown empty when it
        // is displayed without going through the sidebar navigation switch.
        refreshHistory();
    }

    public void refreshHistory() {
        historyListContainer.removeAll();
        List<QuizResult> list = storageService.loadHistory();

        if (list.isEmpty()) {
            JLabel emptyLabel = new JLabel("No past quiz records found yet. Complete a quiz to view history!");
            emptyLabel.setFont(AppTheme.FONT_BODY);
            emptyLabel.setForeground(AppTheme.TEXT_MUTED);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            historyListContainer.add(emptyLabel);
        } else {
            for (QuizResult r : list) {
                historyListContainer.add(createHistoryCard(r));
                historyListContainer.add(Box.createVerticalStrut(12));
            }
        }

        historyListContainer.revalidate();
        historyListContainer.repaint();
    }

    private JPanel createHistoryCard(QuizResult r) {
        RoundedCard card = new RoundedCard(12, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left info: Category, date, duration
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
        left.setOpaque(false);

        JLabel catLabel = new JLabel(r.getCategory());
        catLabel.setFont(AppTheme.FONT_BODY_BOLD);
        catLabel.setForeground(AppTheme.TEXT_PRIMARY);
        left.add(catLabel);

        JLabel timeLabel = new JLabel(formatTimestamp(r.getTimestamp()) + " · Duration: " + r.getFormattedDuration());
        timeLabel.setFont(AppTheme.FONT_SMALL);
        timeLabel.setForeground(AppTheme.TEXT_MUTED);
        left.add(timeLabel);

        card.add(left, BorderLayout.WEST);

        // Right info: Badges
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        Color scoreColor = r.getScorePercent() >= 80 ? AppTheme.ACCENT_GREEN :
                           r.getScorePercent() >= 60 ? AppTheme.ACCENT_AMBER : AppTheme.ACCENT_RED;

        PillBadge scorePill = new PillBadge(r.getScorePercent() + "%", scoreColor);
        right.add(scorePill);

        PillBadge breakdownPill = new PillBadge(r.getCorrect() + " / " + r.getTotalQuestions() + " Correct", AppTheme.TEXT_SECONDARY);
        right.add(breakdownPill);

        PillBadge gradePill = new PillBadge("Grade " + r.getGrade(), AppTheme.ACCENT_AMBER);
        right.add(gradePill);

        card.add(right, BorderLayout.EAST);
        return card;
    }

    /**
     * UX-13: turns the stored ISO timestamp (e.g. {@code 2026-10-28T09:45:10.123})
     * into a human-readable string such as {@code Oct 28, 2026 · 09:45 AM}.
     * Falls back to the raw value if it cannot be parsed.
     */
    private String formatTimestamp(String isoTimestamp) {
        if (isoTimestamp == null || isoTimestamp.isEmpty()) {
            return "Unknown date";
        }
        try {
            LocalDateTime dt = LocalDateTime.parse(isoTimestamp);
            return dt.format(DISPLAY_FORMAT);
        } catch (DateTimeParseException ignored) {
            // Older records may have stored a plain date only
            try {
                return LocalDate.parse(isoTimestamp).format(DATE_ONLY_FORMAT);
            } catch (DateTimeParseException ignored2) {
                return isoTimestamp;
            }
        }
    }
}
