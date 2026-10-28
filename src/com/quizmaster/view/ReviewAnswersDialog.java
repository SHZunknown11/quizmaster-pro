package com.quizmaster.view;

import com.quizmaster.components.PillBadge;
import com.quizmaster.components.PrimaryButton;
import com.quizmaster.components.RoundedCard;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizSession;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Modal dialog showing a question-by-question breakdown of the quiz:
 * user's answer, correct answer, and explanation.
 */
public class ReviewAnswersDialog extends JDialog {

    public ReviewAnswersDialog(Window owner, QuizSession session) {
        super(owner, "Quiz Review — " + session.getCategory(), ModalityType.APPLICATION_MODAL);

        setSize(760, 600);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());

        initUI(session);
    }

    private void initUI(QuizSession session) {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel title = new JLabel("Question-by-Question Review");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        PillBadge scorePill = new PillBadge(session.getScorePercent() + "% Score", AppTheme.ACCENT_AMBER);
        header.add(scorePill, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Scrollable Question List
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(0, 24, 16, 24));

        List<Question> questions = session.getQuestions();
        int[] userAnswers = session.getSelectedAnswers();
        QuizSession.AnswerState[] states = session.getAnswerStates();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int selected = userAnswers[i];
            QuizSession.AnswerState state = states[i];

            listPanel.add(createQuestionReviewCard(i + 1, q, selected, state));
            listPanel.add(Box.createVerticalStrut(14));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Footer with Close Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 16));
        footer.setOpaque(false);
        PrimaryButton closeBtn = new PrimaryButton("Close Review");
        closeBtn.setPreferredSize(new Dimension(140, 40));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createQuestionReviewCard(int qNum, Question q, int selectedIdx, QuizSession.AnswerState state) {
        Color borderColor = (state == QuizSession.AnswerState.CORRECT) ? AppTheme.ACCENT_GREEN :
                            (state == QuizSession.AnswerState.WRONG) ? AppTheme.ACCENT_RED : AppTheme.BORDER_SUBTLE;

        RoundedCard card = new RoundedCard(12, AppTheme.BG_CARD, borderColor, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Question Title Row
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel qLabel = new JLabel("<html><body style='width: 580px;'><b>Q" + qNum + ":</b> " + q.getQuestionText() + "</body></html>");
        qLabel.setFont(AppTheme.FONT_BODY_BOLD);
        qLabel.setForeground(AppTheme.TEXT_PRIMARY);
        top.add(qLabel, BorderLayout.CENTER);

        String badgeText = (state == QuizSession.AnswerState.CORRECT) ? "✓ Correct" :
                           (state == QuizSession.AnswerState.WRONG) ? "✕ Wrong" : "→ Skipped";
        PillBadge badge = new PillBadge(badgeText, borderColor);
        top.add(badge, BorderLayout.EAST);
        card.add(top);
        card.add(Box.createVerticalStrut(10));

        // Selected Option Info
        String[] opts = q.getOptions();
        String selectedText = (selectedIdx >= 0 && selectedIdx < opts.length) ? opts[selectedIdx] : "None (Skipped)";
        JLabel userAnsLabel = new JLabel("Your Answer: " + selectedText);
        userAnsLabel.setFont(AppTheme.FONT_SMALL);
        // BUG-11: a skipped answer is neutral, not an error — don't paint it red.
        Color userAnsColor = (state == QuizSession.AnswerState.CORRECT) ? AppTheme.ACCENT_GREEN :
                             (state == QuizSession.AnswerState.WRONG) ? AppTheme.ACCENT_RED :
                             AppTheme.ACCENT_SLATE;
        userAnsLabel.setForeground(userAnsColor);
        userAnsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(userAnsLabel);

        // Correct Option Info (if user was wrong or skipped)
        if (state != QuizSession.AnswerState.CORRECT) {
            JLabel correctAnsLabel = new JLabel("Correct Answer: " + opts[q.getCorrectIndex()]);
            correctAnsLabel.setFont(AppTheme.FONT_SMALL_BOLD);
            correctAnsLabel.setForeground(AppTheme.ACCENT_GREEN);
            correctAnsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(correctAnsLabel);
        }

        // Explanation
        if (q.getExplanation() != null && !q.getExplanation().isEmpty()) {
            card.add(Box.createVerticalStrut(6));
            JLabel expLabel = new JLabel("<html><body style='width: 620px; color: #94A3B8;'><i>Explanation: " + q.getExplanation() + "</i></body></html>");
            expLabel.setFont(AppTheme.FONT_SMALL);
            expLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(expLabel);
        }

        return card;
    }
}
