package com.quizmaster.view;

import com.quizmaster.components.*;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuizResult;
import com.quizmaster.model.QuizSession;
import com.quizmaster.service.AudioService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.util.function.Consumer;

/**
 * Screen: Active Quiz Screen (The Engine).
 * Renders question progress, circular countdown timer, 4 interactive MCQ cards,
 * 50:50 and hint lifelines, explanation card, and smooth navigation.
 */
public class QuizPanel extends JPanel {

    private final Consumer<QuizResult> onQuizComplete;
    private final Runnable onQuitQuiz;

    private QuizSession session;
    private int secondsRemaining;
    private Timer countdownTimer;
    private boolean answerConfirmed = false;
    private int selectedOptionIndex = -1;

    // UI Components
    private JLabel breadcrumbLabel;
    private JComponent progressBar;
    private JComponent timerArcComponent;
    private JLabel questionTextLabel;
    private JPanel codeSnippetPanel;
    private JTextArea codeSnippetArea;
    private OptionCardButton[] optionButtons;
    private JPanel explanationPanel;
    private JLabel explanationLabel;
    private SecondaryButton fiftyFiftyBtn;
    private SecondaryButton hintBtn;
    private SecondaryButton skipBtn;
    private PrimaryButton actionBtn;

    public QuizPanel(Consumer<QuizResult> onQuizComplete, Runnable onQuitQuiz) {
        this.onQuizComplete = onQuizComplete;
        this.onQuitQuiz = onQuitQuiz;

        setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 36, 20, 36));

        initUI();
        setupKeyBindings();
        setupVisibilityGuard();
    }

    /**
     * BUG-12: Stops the countdown whenever this panel stops being shown
     * (e.g. the user navigates away via the sidebar while a quiz is active),
     * so a stray timer tick can never trigger a ghost screen transition.
     */
    private void setupVisibilityGuard() {
        addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && !isShowing()) {
                    stopTimer();
                }
            }
        });
    }

    private void initUI() {
        // ── Top Header Bar ─────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel topLeft = new JPanel(new GridLayout(2, 1, 0, 4));
        topLeft.setOpaque(false);

        breadcrumbLabel = new JLabel("Java Fundamentals / Question 01 of 10");
        breadcrumbLabel.setFont(AppTheme.FONT_SUBHEADING);
        breadcrumbLabel.setForeground(AppTheme.ACCENT_AMBER);
        topLeft.add(breadcrumbLabel);

        // Progress bar strip
        progressBar = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                int w = getWidth();
                int h = getHeight();

                g2.setColor(AppTheme.BG_CARD_HOVER);
                g2.fillRoundRect(0, 0, w, h, h, h);

                if (session != null && session.getTotalQuestions() > 0) {
                    float pct = (float) (session.getCurrentIndex()) / session.getTotalQuestions();
                    int fillW = Math.max(h, (int) (w * pct));
                    g2.setColor(AppTheme.ACCENT_AMBER);
                    g2.fillRoundRect(0, 0, fillW, h, h, h);
                }
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(280, 6);
            }
        };
        topLeft.add(progressBar);
        topBar.add(topLeft, BorderLayout.WEST);

        // Top Right: Timer Badge + Quit button
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        topRight.setOpaque(false);

        timerArcComponent = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);

                int size = 44;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                int stroke = 4;

                // Track
                g2.setColor(AppTheme.BG_CARD_HOVER);
                g2.setStroke(new BasicStroke(stroke));
                g2.drawOval(x + stroke / 2, y + stroke / 2, size - stroke, size - stroke);

                // Active arc
                int totalSecs = (session != null) ? session.getTimerSecondsPerQuestion() : 30;
                float fraction = (float) secondsRemaining / totalSecs;
                float extent = -fraction * 360f;

                Color arcColor = secondsRemaining <= 5 ? AppTheme.ACCENT_RED :
                                 secondsRemaining <= 10 ? AppTheme.ACCENT_AMBER : AppTheme.ACCENT_BLUE;

                g2.setColor(arcColor);
                g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Arc2D.Float arc = new Arc2D.Float(x + stroke / 2, y + stroke / 2, size - stroke, size - stroke, 90f, extent, Arc2D.OPEN);
                g2.draw(arc);

                // Secs text
                g2.setFont(AppTheme.FONT_SMALL_BOLD);
                g2.setColor(arcColor);
                FontMetrics fm = g2.getFontMetrics();
                String secText = String.valueOf(secondsRemaining);
                int tx = (getWidth() - fm.stringWidth(secText)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(secText, tx, ty);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(48, 48);
            }
        };
        topRight.add(timerArcComponent);

        JButton quitBtn = new JButton("Quit ✕") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                g2.setColor(AppTheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(AppTheme.BORDER_SUBTLE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setFont(AppTheme.FONT_SMALL);
                g2.setColor(AppTheme.TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        quitBtn.setFocusPainted(false);
        quitBtn.setBorderPainted(false);
        quitBtn.setContentAreaFilled(false);
        quitBtn.setOpaque(false);
        quitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        quitBtn.setPreferredSize(new Dimension(68, 32));
        quitBtn.addActionListener(e -> {
            stopTimer();
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to quit the quiz? Current progress will be lost.",
                    "Quit Quiz",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                if (onQuitQuiz != null) onQuitQuiz.run();
            } else {
                resumeTimer();
            }
        });
        topRight.add(quitBtn);

        topBar.add(topRight, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Center Question Area ───────────────────────────────────────
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(12, 0, 8, 0));

        // Lifelines Row
        JPanel lifelinesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        lifelinesRow.setOpaque(false);
        lifelinesRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        fiftyFiftyBtn = new SecondaryButton("50:50 Lifeline");
        fiftyFiftyBtn.setPreferredSize(new Dimension(130, 34));
        fiftyFiftyBtn.setFont(AppTheme.FONT_SMALL_BOLD);
        fiftyFiftyBtn.addActionListener(e -> useFiftyFifty());
        lifelinesRow.add(fiftyFiftyBtn);

        hintBtn = new SecondaryButton("View Hint");
        hintBtn.setPreferredSize(new Dimension(100, 34));
        hintBtn.setFont(AppTheme.FONT_SMALL_BOLD);
        hintBtn.addActionListener(e -> useHint());
        lifelinesRow.add(hintBtn);

        centerPanel.add(lifelinesRow);
        centerPanel.add(Box.createVerticalStrut(10));

        // Question Card
        RoundedCard questionCard = new RoundedCard(AppTheme.CARD_RADIUS, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1);
        questionCard.setLayout(new BorderLayout());
        questionCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        questionCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        questionTextLabel = new JLabel("Question text will appear here...");
        questionTextLabel.setFont(AppTheme.FONT_HEADING);
        questionTextLabel.setForeground(AppTheme.TEXT_PRIMARY);
        questionCard.add(questionTextLabel, BorderLayout.CENTER);

        centerPanel.add(questionCard);
        centerPanel.add(Box.createVerticalStrut(10));

        // Code snippet box (conditionally visible)
        codeSnippetPanel = new JPanel(new BorderLayout());
        codeSnippetPanel.setOpaque(false);
        codeSnippetPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        codeSnippetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        codeSnippetArea = new JTextArea();
        codeSnippetArea.setFont(AppTheme.FONT_CODE);
        codeSnippetArea.setForeground(AppTheme.ACCENT_AMBER);
        codeSnippetArea.setBackground(AppTheme.BG_SURFACE_DARK);
        codeSnippetArea.setBorder(new EmptyBorder(8, 12, 8, 12));
        codeSnippetArea.setEditable(false);

        // UX-14: long snippets must be scrollable instead of silently clipped
        JScrollPane codeScroll = new JScrollPane(codeSnippetArea);
        codeScroll.setOpaque(false);
        codeScroll.getViewport().setOpaque(false);
        codeScroll.setBorder(null);
        codeScroll.setViewportBorder(null);
        codeScroll.getViewport().setBackground(AppTheme.BG_SURFACE_DARK);
        codeScroll.getVerticalScrollBar().setUnitIncrement(14);
        codeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        codeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        codeSnippetPanel.add(codeScroll, BorderLayout.CENTER);
        codeSnippetPanel.setVisible(false);

        centerPanel.add(codeSnippetPanel);
        centerPanel.add(Box.createVerticalStrut(10));

        // Option Choices Grid
        JPanel optionsContainer = new JPanel(new GridLayout(4, 1, 0, 8));
        optionsContainer.setOpaque(false);
        optionsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        optionButtons = new OptionCardButton[4];
        char[] letters = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < 4; i++) {
            final int optIdx = i;
            optionButtons[i] = new OptionCardButton(letters[i], "Option " + letters[i], () -> selectOption(optIdx));
            optionsContainer.add(optionButtons[i]);
        }
        centerPanel.add(optionsContainer);
        centerPanel.add(Box.createVerticalStrut(10));

        // Explanation / Hint Panel (hidden until confirmed or requested)
        explanationPanel = new RoundedCard(10, AppTheme.BG_CARD_HOVER, AppTheme.BORDER_SUBTLE, 1);
        explanationPanel.setLayout(new BorderLayout());
        explanationPanel.setBorder(new EmptyBorder(10, 14, 10, 14));
        explanationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        explanationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        explanationLabel = new JLabel("");
        explanationLabel.setFont(AppTheme.FONT_SMALL);
        explanationLabel.setForeground(AppTheme.TEXT_SECONDARY);
        explanationPanel.add(explanationLabel, BorderLayout.CENTER);
        explanationPanel.setVisible(false);

        centerPanel.add(explanationPanel);

        add(centerPanel, BorderLayout.CENTER);

        // ── Bottom Action Bar ──────────────────────────────────────────
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(8, 0, 0, 0));

        skipBtn = new SecondaryButton("Skip Question →");
        skipBtn.setPreferredSize(new Dimension(150, 44));
        skipBtn.addActionListener(e -> skipQuestion());
        bottomBar.add(skipBtn, BorderLayout.WEST);

        actionBtn = new PrimaryButton("Confirm Answer →");
        actionBtn.setPreferredSize(new Dimension(190, 44));
        actionBtn.addActionListener(e -> onActionButtonClicked());
        bottomBar.add(actionBtn, BorderLayout.EAST);

        add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * Start a fresh quiz session.
     */
    public void startSession(QuizSession newSession) {
        if (newSession == null) return;
        this.session = newSession;
        this.answerConfirmed = false;
        this.selectedOptionIndex = -1;
        loadQuestion(session.getCurrentQuestion());
    }

    private void loadQuestion(Question q) {
        if (session == null || q == null) return;
        answerConfirmed = false;
        selectedOptionIndex = -1;
        explanationPanel.setVisible(false);
        explanationLabel.setText("");

        breadcrumbLabel.setText(session.getCategory() + " / Question " +
                String.format("%02d", session.getCurrentIndex() + 1) + " of " +
                String.format("%02d", session.getTotalQuestions()));

        // Format question and check for code snippet
        String text = q.getQuestionText();
        if (text.contains("```")) {
            String[] parts = text.split("```");
            questionTextLabel.setText("<html><body style='width: 580px;'>" + parts[0].trim() + "</body></html>");
            if (parts.length > 1) {
                codeSnippetArea.setText(parts[1].trim());
                codeSnippetArea.setCaretPosition(0);
                codeSnippetPanel.setVisible(true);
            } else {
                codeSnippetPanel.setVisible(false);
            }
        } else {
            questionTextLabel.setText("<html><body style='width: 580px;'>" + text + "</body></html>");
            codeSnippetPanel.setVisible(false);
        }

        // Setup options
        String[] opts = q.getOptions();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(i < opts.length ? opts[i] : "");
            optionButtons[i].setState(OptionCardButton.CardState.DEFAULT);
        }

        // Reset lifeline button states
        fiftyFiftyBtn.setEnabled(!session.isFiftyFiftyUsed());
        hintBtn.setEnabled(!session.isHintUsed());

        actionBtn.setText("Confirm Answer →");
        actionBtn.setEnabled(false);
        skipBtn.setEnabled(true);

        progressBar.repaint();

        // Start countdown timer for this question
        startQuestionTimer();
    }

    private void startQuestionTimer() {
        stopTimer();
        if (session == null) return;
        secondsRemaining = session.getTimerSecondsPerQuestion();
        timerArcComponent.repaint();

        countdownTimer = new Timer(1000, (ActionEvent e) -> {
            // BUG-12: ignore ticks once the panel is hidden or the session is gone
            if (session == null || !isShowing()) {
                stopTimer();
                return;
            }
            secondsRemaining--;
            timerArcComponent.repaint();

            if (secondsRemaining <= 0) {
                stopTimer();
                // Timeout: auto-confirm the pending selection, otherwise auto-skip
                if (answerConfirmed) {
                    return;
                }
                if (selectedOptionIndex != -1) {
                    confirmAnswer();
                } else {
                    skipQuestion();
                }
            }
        });
        countdownTimer.start();
    }

    private void stopTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }

    private void resumeTimer() {
        if (countdownTimer != null && !answerConfirmed) {
            countdownTimer.start();
        }
    }

    private void selectOption(int index) {
        if (answerConfirmed) return;

        selectedOptionIndex = index;
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].getState() != OptionCardButton.CardState.ELIMINATED) {
                optionButtons[i].setState(i == index ?
                        OptionCardButton.CardState.SELECTED :
                        OptionCardButton.CardState.DEFAULT);
            }
        }
        actionBtn.setEnabled(true);
    }

    private void onActionButtonClicked() {
        if (!answerConfirmed) {
            confirmAnswer();
        } else {
            advanceToNext();
        }
    }

    private void confirmAnswer() {
        if (answerConfirmed || session == null) return;
        // BUG-06: snapshot the selection and lock immediately so no later event
        // (timer tick / key binding / click) can mutate state mid-submission.
        final int chosenIndex = selectedOptionIndex;
        if (chosenIndex == -1) return;

        answerConfirmed = true;
        stopTimer();

        Question q = session.getCurrentQuestion();
        boolean correct = session.submitAnswer(chosenIndex);

        // Highlight correct and wrong options
        for (int i = 0; i < 4; i++) {
            if (i == q.getCorrectIndex()) {
                optionButtons[i].setState(OptionCardButton.CardState.CORRECT);
            } else if (i == chosenIndex && !correct) {
                optionButtons[i].setState(OptionCardButton.CardState.WRONG);
            } else if (optionButtons[i].getState() != OptionCardButton.CardState.ELIMINATED) {
                optionButtons[i].setState(OptionCardButton.CardState.DEFAULT);
            }
        }

        // Audio feedback
        if (correct) {
            AudioService.playCorrect();
        } else {
            AudioService.playWrong();
        }

        // Show Explanation
        if (q.getExplanation() != null && !q.getExplanation().isEmpty()) {
            explanationLabel.setText("<html><body style='width: 580px;'><b>Explanation:</b> " + q.getExplanation() + "</body></html>");
            explanationPanel.setVisible(true);
        }

        skipBtn.setEnabled(false);
        fiftyFiftyBtn.setEnabled(false);
        hintBtn.setEnabled(false);

        if (session.hasNext()) {
            actionBtn.setText("Next Question →");
        } else {
            actionBtn.setText("View Results 🎉");
        }
        actionBtn.setEnabled(true);
    }

    private void skipQuestion() {
        if (answerConfirmed || session == null) return;
        // BUG-03: lock immediately so a racing timer tick cannot skip twice
        answerConfirmed = true;
        stopTimer();
        session.skipCurrent();
        advanceToNext();
    }

    private void advanceToNext() {
        if (session == null) return;
        if (session.hasNext()) {
            session.moveNext();
            loadQuestion(session.getCurrentQuestion());
        } else {
            // Quiz completed!
            stopTimer();
            AudioService.playComplete();
            QuizResult result = session.buildResult();
            if (onQuizComplete != null) {
                onQuizComplete.accept(result);
            }
        }
    }

    private void useFiftyFifty() {
        if (session == null || answerConfirmed || session.isFiftyFiftyUsed()) return;
        int[] eliminated = session.useFiftyFifty();
        if (eliminated != null) {
            for (int idx : eliminated) {
                optionButtons[idx].setState(OptionCardButton.CardState.ELIMINATED);
            }
            fiftyFiftyBtn.setEnabled(false);
            AudioService.playClick();
        }
    }

    private void useHint() {
        if (session == null || answerConfirmed || session.isHintUsed()) return;
        String hint = session.useHint();
        if (hint != null) {
            explanationLabel.setText("<html><body style='width: 580px;'><b>Hint:</b> " + hint + "</body></html>");
            explanationPanel.setVisible(true);
            hintBtn.setEnabled(false);
            AudioService.playClick();
        }
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        char[] keys = {'1', '2', '3', '4', 'a', 'b', 'c', 'd'};
        for (int i = 0; i < keys.length; i++) {
            char k = keys[i];
            final int optIdx = i % 4;
            im.put(KeyStroke.getKeyStroke(k), "select_opt_" + k);
            im.put(KeyStroke.getKeyStroke(Character.toUpperCase(k)), "select_opt_" + Character.toUpperCase(k));
            am.put("select_opt_" + k, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!answerConfirmed && optionButtons[optIdx].getState() != OptionCardButton.CardState.ELIMINATED) {
                        selectOption(optIdx);
                    }
                }
            });
            am.put("select_opt_" + Character.toUpperCase(k), am.get("select_opt_" + k));
        }

        // Enter key confirms answer or advances
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter_action");
        am.put("enter_action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actionBtn.isEnabled()) {
                    onActionButtonClicked();
                }
            }
        });
    }
}
