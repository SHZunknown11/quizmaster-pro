package com.quizmaster.model;

/**
 * Represents a single MCQ question with its options, correct answer, hint, and explanation.
 * Immutable once constructed.
 */
public class Question {

    private final String id;
    private final String category;
    private final String difficulty;   // "EASY", "MEDIUM", "HARD"
    private final String questionText;
    private final String[] options;    // exactly 4 options
    private final int correctIndex;    // 0-based index into options
    private final String hint;
    private final String explanation;

    public Question(String id, String category, String difficulty,
                    String questionText, String[] options, int correctIndex,
                    String hint, String explanation) {
        this.id = id;
        this.category = category;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
        this.hint = hint;
        this.explanation = explanation;
    }

    public String getId()           { return id; }
    public String getCategory()     { return category; }
    public String getDifficulty()   { return difficulty; }
    public String getQuestionText() { return questionText; }
    public String[] getOptions()    { return options; }
    public int getCorrectIndex()    { return correctIndex; }
    public String getHint()         { return hint; }
    public String getExplanation()  { return explanation; }

    /**
     * Returns true if the given 0-based index is the correct answer.
     */
    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctIndex;
    }

    @Override
    public String toString() {
        return "Question{" + id + ": " + questionText.substring(0, Math.min(40, questionText.length())) + "...}";
    }
}
