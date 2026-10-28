package com.quizmaster.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores user profile statistics that persist between sessions.
 */
public class UserProfile {

    private String username;
    private String displayName;
    private int totalQuizzes;
    private int bestScore;         // percentage
    private int averageScore;      // percentage
    private int dayStreak;
    private String lastQuizDate;   // ISO date "2026-10-27"
    private int timerPreference;   // seconds per question (15 / 30 / 45 / 60)
    private List<ActivityEntry> recentActivity;

    public UserProfile() {
        this.username = "user";
        this.displayName = "User";
        this.totalQuizzes = 0;
        this.bestScore = 0;
        this.averageScore = 0;
        this.dayStreak = 0;
        this.lastQuizDate = "";
        this.timerPreference = DEFAULT_TIMER_SECONDS;
        this.recentActivity = new ArrayList<>();
    }

    /** Default seconds per question when the user has no saved preference. */
    public static final int DEFAULT_TIMER_SECONDS = 30;

    // ── Getters ────────────────────────────────────────────────────
    public String getUsername()        { return username; }
    public String getDisplayName()    { return displayName; }
    public int getTotalQuizzes()      { return totalQuizzes; }
    public int getBestScore()         { return bestScore; }
    public int getAverageScore()      { return averageScore; }
    public int getDayStreak()         { return dayStreak; }
    public String getLastQuizDate()   { return lastQuizDate; }
    public int getTimerPreference()   { return timerPreference > 0 ? timerPreference : DEFAULT_TIMER_SECONDS; }
    public List<ActivityEntry> getRecentActivity() { return recentActivity; }

    // ── Setters ────────────────────────────────────────────────────
    public void setUsername(String username)         { this.username = username; }
    public void setDisplayName(String displayName)   { this.displayName = displayName; }
    public void setTotalQuizzes(int totalQuizzes)    { this.totalQuizzes = totalQuizzes; }
    public void setBestScore(int bestScore)          { this.bestScore = bestScore; }
    public void setAverageScore(int averageScore)    { this.averageScore = averageScore; }
    public void setDayStreak(int dayStreak)          { this.dayStreak = dayStreak; }
    public void setLastQuizDate(String lastQuizDate) { this.lastQuizDate = lastQuizDate; }
    public void setTimerPreference(int timerPreference) {
        this.timerPreference = timerPreference > 0 ? timerPreference : DEFAULT_TIMER_SECONDS;
    }
    public void setRecentActivity(List<ActivityEntry> recentActivity) {
        this.recentActivity = recentActivity;
    }

    /**
     * Updates stats after a quiz is completed.
     */
    public void recordQuiz(String category, int scorePercent) {
        totalQuizzes++;
        if (scorePercent > bestScore) {
            bestScore = scorePercent;
        }
        // Rolling average
        averageScore = ((averageScore * (totalQuizzes - 1)) + scorePercent) / totalQuizzes;

        // Streak logic
        String today = java.time.LocalDate.now().toString();
        String yesterday = java.time.LocalDate.now().minusDays(1).toString();
        if (today.equals(lastQuizDate)) {
            // Same day, streak unchanged
        } else if (yesterday.equals(lastQuizDate)) {
            dayStreak++;
        } else {
            dayStreak = 1;
        }
        lastQuizDate = today;

        // Add to recent activity (keep max 5)
        recentActivity.add(0, new ActivityEntry(category, scorePercent, today));
        if (recentActivity.size() > 5) {
            recentActivity = new ArrayList<>(recentActivity.subList(0, 5));
        }
    }

    /**
     * An entry in the recent activity feed.
     */
    public static class ActivityEntry {
        private final String category;
        private final int scorePercent;
        private final String date; // ISO date

        public ActivityEntry(String category, int scorePercent, String date) {
            this.category = category;
            this.scorePercent = scorePercent;
            this.date = date;
        }

        public String getCategory()    { return category; }
        public int getScorePercent()   { return scorePercent; }
        public String getDate()        { return date; }

        /**
         * Returns a human-readable relative time string.
         */
        public String getRelativeTime() {
            try {
                java.time.LocalDate entryDate = java.time.LocalDate.parse(date);
                long days = java.time.temporal.ChronoUnit.DAYS.between(entryDate, java.time.LocalDate.now());
                if (days == 0) return "Today";
                if (days == 1) return "Yesterday";
                return days + " days ago";
            } catch (Exception e) {
                return date;
            }
        }
    }
}
