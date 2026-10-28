package com.quizmaster.view;

import com.quizmaster.components.PillBadge;
import com.quizmaster.components.PrimaryButton;
import com.quizmaster.components.RoundedCard;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizSession;
import com.quizmaster.util.AppTheme;
import com.quizmaster.util.DarkScrollBarUI;
import com.quizmaster.util.HtmlText;
import com.quizmaster.util.UiKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Modal dialog showing a question-by-question breakdown of the quiz:
 * user's answer, correct answer, and explanation.
 */
public class ReviewAnswersDialog extends JDialog {

    // Wrap widths sized for the 760px dialog (card padding, list padding and scroll bar subtracted)
    private static final int QUESTION_WIDTH = 520;
    private static final int EXPLANATION_WIDTH = 620;

    public ReviewAnswersDialog(Window owner, QuizSession session) {
        super(owner, "Quiz Review — " + session.getCategory(), ModalityType.APPLICATION_MODAL);

        setSize(760, 600);
        setMinimumSize(new Dimension(700, 400));
        setLocationRelativeTo(owner);
        getContentPane().setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());

        initUI(session);

        // Esc closes the review
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
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
        header.add(UiKit.verticallyCentered(scorePill), BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Scrollable Question List
        JPanel listPanel = new UiKit.ViewportWidthPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
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
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        DarkScrollBarUI.style(scroll);
        add(scroll, BorderLayout.CENTER);

        // Footer with Close Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 16));
        footer.setOpaque(false);
        PrimaryButton closeBtn = new PrimaryButton("Close Review");
        closeBtn.setPreferredSize(new Dimension(140, 40));
        closeBtn.addActionListener((ActionEvent e) -> dispose());
        footer.add(closeBtn);

        add(footer, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(null);
    }

    private JPanel createQuestionReviewCard(int qNum, Question q, int selectedIdx, QuizSession.AnswerState state) {
        Color borderColor = (state == QuizSession.AnswerState.CORRECT) ? AppTheme.ACCENT_GREEN :
                            (state == QuizSession.AnswerState.WRONG) ? AppTheme.ACCENT_RED : AppTheme.BORDER_SUBTLE;

        RoundedCard card = new RoundedCard(12, AppTheme.BG_CARD, borderColor, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // A ``` fence in the question text is shown as a monospace code block
        String text = q.getQuestionText();
        String code = null;
        if (text.contains("```")) {
            String[] parts = text.split("```");
            StringBuilder stem = new StringBuilder(parts[0].trim());
            if (parts.length > 2 && !parts[2].trim().isEmpty()) {
                stem.append(' ').append(parts[2].trim());
            }
            text = stem.toString();
            if (parts.length > 1) {
                code = parts[1].trim();
            }
        }

        // Question Title Row
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel qLabel = new JLabel(HtmlText.html("<b>Q" + qNum + ":</b> " + HtmlText.escape(text), QUESTION_WIDTH));
        qLabel.setFont(AppTheme.FONT_BODY_BOLD);
        qLabel.setForeground(AppTheme.TEXT_PRIMARY);
        qLabel.setVerticalAlignment(SwingConstants.TOP);
        top.add(qLabel, BorderLayout.CENTER);

        String badgeText = (state == QuizSession.AnswerState.CORRECT) ? "Correct" :
                           (state == QuizSession.AnswerState.WRONG) ? "Wrong" : "Skipped";
        PillBadge.Glyph glyph = (state == QuizSession.AnswerState.CORRECT) ? PillBadge.Glyph.CHECK :
                                (state == QuizSession.AnswerState.WRONG) ? PillBadge.Glyph.CROSS : PillBadge.Glyph.ARROW;
        Color badgeColor = (state == QuizSession.AnswerState.CORRECT || state == QuizSession.AnswerState.WRONG)
                ? borderColor : AppTheme.ACCENT_SLATE;
        JPanel badgeHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeHolder.setOpaque(false);
        badgeHolder.add(new PillBadge(badgeText, badgeColor, glyph));
        top.add(badgeHolder, BorderLayout.EAST);
        card.add(top);

        if (code != null && !code.isEmpty()) {
            card.add(Box.createVerticalStrut(8));
            card.add(createCodeBlock(code));
        }
        card.add(Box.createVerticalStrut(10));

        // Selected Option Info
        String[] opts = q.getOptions();
        String selectedText = (selectedIdx >= 0 && selectedIdx < opts.length) ? opts[selectedIdx] : "None (Skipped)";
        JLabel userAnsLabel = new JLabel(HtmlText.paragraph("Your Answer: " + selectedText, EXPLANATION_WIDTH));
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
            String correct = q.getCorrectIndex() >= 0 && q.getCorrectIndex() < opts.length
                    ? opts[q.getCorrectIndex()] : "";
            JLabel correctAnsLabel = new JLabel(HtmlText.paragraph("Correct Answer: " + correct, EXPLANATION_WIDTH));
            correctAnsLabel.setFont(AppTheme.FONT_SMALL_BOLD);
            correctAnsLabel.setForeground(AppTheme.ACCENT_GREEN);
            correctAnsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(correctAnsLabel);
        }

        // Explanation
        if (q.getExplanation() != null && !q.getExplanation().isEmpty()) {
            card.add(Box.createVerticalStrut(6));
            JLabel expLabel = new JLabel(HtmlText.html(
                    "<i>Explanation: " + HtmlText.escape(q.getExplanation()) + "</i>", EXPLANATION_WIDTH));
            expLabel.setFont(AppTheme.FONT_SMALL);
            expLabel.setForeground(AppTheme.TEXT_SECONDARY);
            expLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(expLabel);
        }

        return card;
    }

    private JComponent createCodeBlock(String code) {
        JTextArea area = new JTextArea(code);
        area.setFont(AppTheme.FONT_CODE);
        area.setForeground(AppTheme.ACCENT_AMBER);
        area.setBackground(AppTheme.BG_SURFACE_DARK);
        area.setBorder(new EmptyBorder(8, 12, 8, 12));
        area.setEditable(false);
        area.setFocusable(false);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        // keep the block at its natural size so BoxLayout cannot stretch it vertically
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, area.getPreferredSize().height));
        return area;
    }
}
