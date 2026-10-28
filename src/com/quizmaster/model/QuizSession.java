package com.quizmaster.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the runtime state of an active quiz session.
 * Tracks current question index, user answers, lifeline usage, and elapsed time.
 */
public class QuizSession {

    public enum AnswerState { UNANSWERED, CORRECT, WRONG, SKIPPED }

    private final List<Question> questions;
    private final int[] selectedAnswers;       // -1 = unanswered/skipped
    private final AnswerState[] answerStates;
    private final String category;

    private int currentIndex;
    private boolean fiftyFiftyUsed;
    private boolean hintUsed;
    private long startTimeMillis;
    private int timerSecondsPerQuestion;

    public QuizSession(List<Question> questions, String category, int timerSecondsPerQuestion) {
        this.questions = new ArrayList<>(questions);
        this.category = category;
        this.selectedAnswers = new int[questions.size()];
        this.answerStates = new AnswerState[questions.size()];
        this.currentIndex = 0;
        this.fiftyFiftyUsed = false;
        this.hintUsed = false;
        this.timerSecondsPerQuestion = timerSecondsPerQuestion;
        this.startTimeMillis = System.currentTimeMillis();

        for (int i = 0; i < questions.size(); i++) {
            selectedAnswers[i] = -1;
            answerStates[i] = AnswerState.UNANSWERED;
        }
    }

    // ── Navigation ─────────────────────────────────────────────────
    public Question getCurrentQuestion() {
        return questions.get(currentIndex);
    }

    public int getCurrentIndex()   { return currentIndex; }
    public int getTotalQuestions()  { return questions.size(); }
    public String getCategory()    { return category; }
    public boolean isFiftyFiftyUsed() { return fiftyFiftyUsed; }
    public boolean isHintUsed()       { return hintUsed; }
    public int getTimerSecondsPerQuestion() { return timerSecondsPerQuestion; }

    public boolean hasNext() {
        return currentIndex < questions.size() - 1;
    }

    public void moveNext() {
        if (hasNext()) {
            currentIndex++;
        }
    }

    public boolean isFinished() {
        return currentIndex >= questions.size() - 1 &&
               answerStates[currentIndex] != AnswerState.UNANSWERED;
    }

    // ── Answer Submission ──────────────────────────────────────────
    public boolean submitAnswer(int optionIndex) {
        Question q = getCurrentQuestion();
        selectedAnswers[currentIndex] = optionIndex;
        boolean correct = q.isCorrect(optionIndex);
        answerStates[currentIndex] = correct ? AnswerState.CORRECT : AnswerState.WRONG;
        return correct;
    }

    public void skipCurrent() {
        selectedAnswers[currentIndex] = -1;
        answerStates[currentIndex] = AnswerState.SKIPPED;
    }

    // ── Lifelines ──────────────────────────────────────────────────
    /**
     * Returns indices of 2 wrong options to eliminate for 50:50 lifeline.
     * Returns null if already used.
     */
    public int[] useFiftyFifty() {
        if (fiftyFiftyUsed) return null;
        fiftyFiftyUsed = true;

        Question q = getCurrentQuestion();
        List<Integer> wrongIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != q.getCorrectIndex()) {
                wrongIndices.add(i);
            }
        }
        // Remove 2 random wrong options
        java.util.Collections.shuffle(wrongIndices);
        return new int[]{ wrongIndices.get(0), wrongIndices.get(1) };
    }

    public String useHint() {
        if (hintUsed) return null;
        hintUsed = true;
        return getCurrentQuestion().getHint();
    }

    // ── Results ────────────────────────────────────────────────────
    public int getCorrectCount() {
        int count = 0;
        for (AnswerState s : answerStates) {
            if (s == AnswerState.CORRECT) count++;
        }
        return count;
    }

    public int getWrongCount() {
        int count = 0;
        for (AnswerState s : answerStates) {
            if (s == AnswerState.WRONG) count++;
        }
        return count;
    }

    /**
     * BUG-07: only explicitly skipped questions are counted. Questions that were
     * never reached stay UNANSWERED and are reported separately so the skipped
     * tally is not inflated.
     */
    public int getSkippedCount() {
        int count = 0;
        for (AnswerState s : answerStates) {
            if (s == AnswerState.SKIPPED) count++;
        }
        return count;
    }

    /**
     * Number of questions that were never reached / never acted upon.
     */
    public int getUnansweredCount() {
        int count = 0;
        for (AnswerState s : answerStates) {
            if (s == AnswerState.UNANSWERED) count++;
        }
        return count;
    }

    public int getScorePercent() {
        return questions.size() > 0 ? (getCorrectCount() * 100) / questions.size() : 0;
    }

    public int getElapsedSeconds() {
        return (int)((System.currentTimeMillis() - startTimeMillis) / 1000);
    }

    public List<Question> getQuestions()       { return questions; }
    public int[] getSelectedAnswers()          { return selectedAnswers; }
    public AnswerState[] getAnswerStates()     { return answerStates; }

    /**
     * Builds a QuizResult from the completed session.
     */
    public QuizResult buildResult() {
        return new QuizResult(
            java.util.UUID.randomUUID().toString(),
            category,
            questions.size(),
            getCorrectCount(),
            getWrongCount(),
            getSkippedCount(),
            getElapsedSeconds(),
            java.time.LocalDateTime.now().toString()
        );
    }
}
