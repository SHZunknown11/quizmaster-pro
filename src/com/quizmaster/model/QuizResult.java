package com.quizmaster.model;

/**
 * Stores the result of a completed quiz session.
 */
public class QuizResult {

    private String id;
    private String category;
    private int totalQuestions;
    private int correct;
    private int wrong;
    private int skipped;
    private int scorePercent;
    private int durationSeconds;
    private String grade;
    private String timestamp;    // ISO datetime

    public QuizResult() {}

    public QuizResult(String id, String category, int totalQuestions,
                      int correct, int wrong, int skipped,
                      int durationSeconds, String timestamp) {
        this.id = id;
        this.category = category;
        this.totalQuestions = totalQuestions;
        this.correct = correct;
        this.wrong = wrong;
        this.skipped = skipped;
        this.scorePercent = totalQuestions > 0 ? (correct * 100) / totalQuestions : 0;
        this.durationSeconds = durationSeconds;
        this.timestamp = timestamp;

        // Calculate grade
        if (scorePercent >= 95) grade = "A+";
        else if (scorePercent >= 80) grade = "A";
        else if (scorePercent >= 65) grade = "B";
        else if (scorePercent >= 50) grade = "C";
        else grade = "F";
    }

    // ── Getters ────────────────────────────────────────────────────
    public String getId()            { return id; }
    public String getCategory()      { return category; }
    public int getTotalQuestions()    { return totalQuestions; }
    public int getCorrect()          { return correct; }
    public int getWrong()            { return wrong; }
    public int getSkipped()          { return skipped; }
    public int getScorePercent()     { return scorePercent; }
    public int getDurationSeconds()  { return durationSeconds; }
    public String getGrade()         { return grade; }
    public String getTimestamp()     { return timestamp; }

    // ── Setters (for JSON deserialization) ──────────────────────────
    public void setId(String id)                       { this.id = id; }
    public void setCategory(String category)           { this.category = category; }
    public void setTotalQuestions(int totalQuestions)   { this.totalQuestions = totalQuestions; }
    public void setCorrect(int correct)                { this.correct = correct; }
    public void setWrong(int wrong)                    { this.wrong = wrong; }
    public void setSkipped(int skipped)                { this.skipped = skipped; }
    public void setScorePercent(int scorePercent)       { this.scorePercent = scorePercent; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public void setGrade(String grade)                  { this.grade = grade; }
    public void setTimestamp(String timestamp)           { this.timestamp = timestamp; }

    /**
     * Returns a formatted duration string like "4m 32s".
     */
    public String getFormattedDuration() {
        int mins = durationSeconds / 60;
        int secs = durationSeconds % 60;
        if (mins > 0) {
            return mins + "m " + secs + "s";
        }
        return secs + "s";
    }

    @Override
    public String toString() {
        return "QuizResult{" + category + " " + scorePercent + "% grade=" + grade + "}";
    }
}
