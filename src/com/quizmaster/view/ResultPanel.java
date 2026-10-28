package com.quizmaster.view;

import com.quizmaster.components.*;
import com.quizmaster.model.QuizResult;
import com.quizmaster.model.QuizSession;
import com.quizmaster.service.AudioService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Screen 2: Result Panel (The Right Mockup).
 * Displays "Quiz Complete!", the animated circular score meter, breakdown pills
 * (Correct, Wrong, Skipped), prominent grade, Try Again / New Quiz buttons,
 * and the "Review Answers →" modal link.
 */
public class ResultPanel extends JPanel {

    private final Runnable onTryAgain;
    private final Runnable onNewQuiz;

    private QuizResult result;
    private QuizSession session;

    private JLabel titleLabel;
    private CircularScoreMeter scoreMeter;
    private PillBadge correctPill;
    private PillBadge wrongPill;
    private PillBadge skippedPill;
    private PillBadge gradePill;
    private JLabel metaLabel;
    private JLabel reviewLink;

    public ResultPanel(Runnable onTryAgain, Runnable onNewQuiz) {
        this.onTryAgain = onTryAgain;
        this.onNewQuiz = onNewQuiz;

        setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initUI();
    }

    private void initUI() {
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);
        centerContainer.add(Box.createVerticalGlue());

        // ── 1. Top Header ──────────────────────────────────────────────
        titleLabel = new JLabel("Quiz Complete!");
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(titleLabel);
        centerContainer.add(Box.createVerticalStrut(20));

        // ── 2. Circular Score Meter (The Hero) ─────────────────────────
        scoreMeter = new CircularScoreMeter();
        scoreMeter.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreMeter.setMaximumSize(new Dimension(220, 220));
        centerContainer.add(scoreMeter);
        centerContainer.add(Box.createVerticalStrut(20));

        // ── 3. Metric Breakdown Pills Row ──────────────────────────────
        JPanel pillRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        pillRow.setOpaque(false);
        pillRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        pillRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        correctPill = new PillBadge("8 Correct", AppTheme.ACCENT_GREEN, PillBadge.Glyph.CHECK);
        wrongPill = new PillBadge("1 Wrong", AppTheme.ACCENT_RED, PillBadge.Glyph.CROSS);
        skippedPill = new PillBadge("1 Skipped", AppTheme.ACCENT_SLATE, PillBadge.Glyph.ARROW);
        gradePill = new PillBadge("Grade A", AppTheme.ACCENT_AMBER);

        pillRow.add(correctPill);
        pillRow.add(wrongPill);
        pillRow.add(skippedPill);
        pillRow.add(gradePill);

        centerContainer.add(pillRow);
        centerContainer.add(Box.createVerticalStrut(14));

        // ── 4. Metadata String (Category · Duration) ───────────────────
        metaLabel = new JLabel("Java Fundamentals · 4m 32s");
        metaLabel.setFont(AppTheme.FONT_BODY);
        metaLabel.setForeground(AppTheme.TEXT_MUTED);
        metaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(metaLabel);
        centerContainer.add(Box.createVerticalStrut(28));

        // ── 5. Action Buttons (Try Again | New Quiz) ───────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        PrimaryButton tryAgainBtn = new PrimaryButton("Try Again");
        tryAgainBtn.setPreferredSize(new Dimension(170, 46));
        tryAgainBtn.addActionListener(e -> {
            if (onTryAgain != null) onTryAgain.run();
        });
        btnRow.add(tryAgainBtn);

        SecondaryButton newQuizBtn = new SecondaryButton("New Quiz");
        newQuizBtn.setPreferredSize(new Dimension(150, 46));
        newQuizBtn.addActionListener(e -> {
            if (onNewQuiz != null) onNewQuiz.run();
        });
        btnRow.add(newQuizBtn);

        centerContainer.add(btnRow);
        centerContainer.add(Box.createVerticalStrut(22));

        // ── 6. "Review Answers →" Text Link ────────────────────────────
        reviewLink = new JLabel("Review Answers →");
        reviewLink.setFont(AppTheme.FONT_SUBHEADING);
        reviewLink.setForeground(AppTheme.ACCENT_BLUE);
        reviewLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        reviewLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        reviewLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // UX-07: the <font> colour keeps the sky-blue accent that plain
                // HTML wrapping would otherwise reset to the default foreground.
                reviewLink.setText("<html><u><font color='#38BDF8'>Review Answers →</font></u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                reviewLink.setText("Review Answers →");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                AudioService.playClick();
                openReviewDialog();
            }
        });
        centerContainer.add(reviewLink);
        centerContainer.add(Box.createVerticalGlue());

        add(centerContainer, BorderLayout.CENTER);
    }

    /**
     * Populate with the finished quiz result & session.
     */
    public void displayResult(QuizResult result, QuizSession session) {
        this.result = result;
        this.session = session;

        String subLabel = result.getCorrect() + " of " + result.getTotalQuestions() + " Correct";
        scoreMeter.setScore(result.getScorePercent(), subLabel);

        correctPill.setText(result.getCorrect() + " Correct");
        wrongPill.setText(result.getWrong() + " Wrong");
        skippedPill.setText(result.getSkipped() + " Skipped");
        gradePill.setText("Grade " + result.getGrade());

        metaLabel.setText(result.getCategory() + " · " + result.getFormattedDuration());

        revalidate();
        repaint();
    }

    private void openReviewDialog() {
        if (session != null) {
            Window window = SwingUtilities.getWindowAncestor(this);
            ReviewAnswersDialog dialog = new ReviewAnswersDialog(window, session);
            dialog.setVisible(true);
        }
    }
}
