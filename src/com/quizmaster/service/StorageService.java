package com.quizmaster.service;

import com.quizmaster.model.QuizResult;
import com.quizmaster.model.UserAccount;
import com.quizmaster.model.UserProfile;
import com.quizmaster.model.UserProfile.ActivityEntry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Handles reading and writing UserAccount credentials, UserProfile statistics,
 * and QuizResult history to flat JSON files.
 */
public class StorageService {

    private final Path dataRoot;
    private final Path usersDir;
    private final Path historyDir;
    private final Path accountsPath;

    private UserAccount currentAccount;

    public StorageService(Path dataRoot) {
        this.dataRoot = dataRoot;
        this.usersDir = dataRoot.resolve("users");
        this.historyDir = dataRoot.resolve("history");
        this.accountsPath = usersDir.resolve("users.json");

        ensureDefaultAccounts();
    }

    // ═══════════════════════════════════════════════════════════════
    //  AUTHENTICATION & ACCOUNTS
    // ═══════════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    public List<UserAccount> loadAccounts() {
        List<UserAccount> accounts = new ArrayList<>();
        try {
            if (Files.exists(accountsPath)) {
                String content = Files.readString(accountsPath, StandardCharsets.UTF_8);
                List<Object> arr = JsonParser.parseArray(content);
                for (Object item : arr) {
                    Map<String, Object> map = (Map<String, Object>) item;
                    UserAccount acc = new UserAccount(
                        JsonParser.getString(map, "username", ""),
                        JsonParser.getString(map, "displayName", ""),
                        JsonParser.getString(map, "salt", ""),
                        JsonParser.getString(map, "passwordHash", ""),
                        JsonParser.getString(map, "createdAt", "")
                    );
                    accounts.add(acc);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
        return accounts;
    }

    public void saveAccounts(List<UserAccount> accounts) {
        try {
            Files.createDirectories(usersDir);
            List<Object> arr = new ArrayList<>();
            for (UserAccount acc : accounts) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("username", acc.getUsername());
                map.put("displayName", acc.getDisplayName());
                map.put("salt", acc.getSalt());
                map.put("passwordHash", acc.getPasswordHash());
                map.put("createdAt", acc.getCreatedAt());
                arr.add(map);
            }
            Files.writeString(accountsPath, JsonParser.toPrettyJson(arr), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error saving accounts: " + e.getMessage());
        }
    }

    public boolean userExists(String username) {
        if (username == null) return false;
        String u = username.trim().toLowerCase();
        for (UserAccount acc : loadAccounts()) {
            if (acc.getUsername().equalsIgnoreCase(u)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Authenticates a user with username and password.
     */
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) return false;
        String u = username.trim().toLowerCase();
        for (UserAccount acc : loadAccounts()) {
            if (acc.getUsername().equalsIgnoreCase(u)) {
                if (acc.verifyPassword(password)) {
                    this.currentAccount = acc;
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Registers a new user account.
     */
    public boolean register(String username, String displayName, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }
        if (userExists(username)) {
            return false;
        }

        UserAccount newAcc = UserAccount.create(username, displayName, password);
        List<UserAccount> accounts = loadAccounts();
        accounts.add(newAcc);
        saveAccounts(accounts);

        this.currentAccount = newAcc;

        // Initialize empty profile for new user
        UserProfile initialProfile = new UserProfile();
        initialProfile.setUsername(newAcc.getUsername());
        initialProfile.setDisplayName(newAcc.getDisplayName());
        initialProfile.setTotalQuizzes(0);
        initialProfile.setBestScore(0);
        initialProfile.setAverageScore(0);
        // a streak only starts once the first quiz is completed (recordQuiz sets it to 1)
        initialProfile.setDayStreak(0);
        initialProfile.setLastQuizDate("");
        initialProfile.setTimerPreference(UserProfile.DEFAULT_TIMER_SECONDS);
        initialProfile.setRecentActivity(new ArrayList<>());
        saveProfile(initialProfile);

        return true;
    }

    public UserAccount getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(UserAccount account) {
        this.currentAccount = account;
    }

    public void logout() {
        this.currentAccount = null;
    }

    private void ensureDefaultAccounts() {
        List<UserAccount> existing = loadAccounts();
        if (existing.isEmpty()) {
            // Seed default "taha" account (password: taha123)
            UserAccount defaultUser = UserAccount.create("taha", "Taha", "taha123");
            existing.add(defaultUser);
            saveAccounts(existing);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PER-USER PROFILE
    // ═══════════════════════════════════════════════════════════════

    private Path resolveUserProfilePath() {
        if (currentAccount == null) {
            return usersDir.resolve("user_profile.json");
        }
        String u = currentAccount.getUsername();
        Path userSpecific = usersDir.resolve("user_" + u + ".json");
        // BUG-19: no username is special-cased any more. If this account has no
        // dedicated profile file yet, generically fall back to the legacy
        // user_profile.json — but only when that legacy file actually belongs to
        // the signed-in account, so users never read each other's stats.
        if (!Files.exists(userSpecific)) {
            Path legacy = usersDir.resolve("user_profile.json");
            if (Files.exists(legacy) && legacyProfileBelongsTo(legacy, u)) {
                return legacy;
            }
        }
        return userSpecific;
    }

    /**
     * Reads only the "username" field of the legacy profile file to decide
     * whether it may be reused for the given account.
     */
    private boolean legacyProfileBelongsTo(Path legacy, String username) {
        try {
            String content = Files.readString(legacy, StandardCharsets.UTF_8);
            Map<String, Object> map = JsonParser.parseObject(content);
            String owner = JsonParser.getString(map, "username", "");
            return owner.isEmpty() || owner.equalsIgnoreCase(username);
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public UserProfile loadProfile() {
        UserProfile profile = new UserProfile();
        Path path = resolveUserProfilePath();

        try {
            if (Files.exists(path)) {
                String content = Files.readString(path, StandardCharsets.UTF_8);
                Map<String, Object> map = JsonParser.parseObject(content);

                profile.setUsername(JsonParser.getString(map, "username", currentAccount != null ? currentAccount.getUsername() : "user"));
                profile.setDisplayName(JsonParser.getString(map, "displayName", currentAccount != null ? currentAccount.getDisplayName() : "User"));
                profile.setTotalQuizzes(JsonParser.getInt(map, "totalQuizzes", 0));
                profile.setBestScore(JsonParser.getInt(map, "bestScore", 0));
                profile.setAverageScore(JsonParser.getInt(map, "averageScore", 0));
                profile.setDayStreak(JsonParser.getInt(map, "dayStreak", 0));
                profile.setLastQuizDate(JsonParser.getString(map, "lastQuizDate", ""));
                profile.setTimerPreference(JsonParser.getInt(map, "timerPreference", UserProfile.DEFAULT_TIMER_SECONDS));

                List<ActivityEntry> activities = new ArrayList<>();
                List<Object> actArr = JsonParser.getArray(map, "recentActivity");
                for (Object item : actArr) {
                    Map<String, Object> a = (Map<String, Object>) item;
                    activities.add(new ActivityEntry(
                        JsonParser.getString(a, "category", ""),
                        JsonParser.getInt(a, "scorePercent", 0),
                        JsonParser.getString(a, "date", "")
                    ));
                }
                profile.setRecentActivity(activities);
            } else if (currentAccount != null) {
                profile.setUsername(currentAccount.getUsername());
                profile.setDisplayName(currentAccount.getDisplayName());
            }
        } catch (Exception e) {
            System.err.println("Error loading profile: " + e.getMessage());
        }
        return profile;
    }

    public void saveProfile(UserProfile profile) {
        Path path = resolveUserProfilePath();
        try {
            Files.createDirectories(path.getParent());

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("username", profile.getUsername());
            map.put("displayName", profile.getDisplayName());
            map.put("totalQuizzes", profile.getTotalQuizzes());
            map.put("bestScore", profile.getBestScore());
            map.put("averageScore", profile.getAverageScore());
            map.put("dayStreak", profile.getDayStreak());
            map.put("lastQuizDate", profile.getLastQuizDate());
            map.put("timerPreference", profile.getTimerPreference());

            List<Object> actList = new ArrayList<>();
            for (ActivityEntry entry : profile.getRecentActivity()) {
                Map<String, Object> a = new LinkedHashMap<>();
                a.put("category", entry.getCategory());
                a.put("scorePercent", entry.getScorePercent());
                a.put("date", entry.getDate());
                actList.add(a);
            }
            map.put("recentActivity", actList);

            Files.writeString(path, JsonParser.toPrettyJson(map), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error saving profile: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PER-USER QUIZ HISTORY
    // ═══════════════════════════════════════════════════════════════

    private Path resolveQuizHistoryPath() {
        if (currentAccount == null) {
            return historyDir.resolve("quiz_history.json");
        }
        String u = currentAccount.getUsername();
        Path userHistory = historyDir.resolve("history_" + u + ".json");
        if (u.equals("taha") && !Files.exists(userHistory) && Files.exists(historyDir.resolve("quiz_history.json"))) {
            return historyDir.resolve("quiz_history.json");
        }
        return userHistory;
    }

    @SuppressWarnings("unchecked")
    public List<QuizResult> loadHistory() {
        List<QuizResult> results = new ArrayList<>();
        Path path = resolveQuizHistoryPath();

        try {
            if (Files.exists(path)) {
                String content = Files.readString(path, StandardCharsets.UTF_8);
                List<Object> arr = JsonParser.parseArray(content);

                for (Object item : arr) {
                    Map<String, Object> r = (Map<String, Object>) item;
                    QuizResult result = new QuizResult();
                    result.setId(JsonParser.getString(r, "id", ""));
                    result.setCategory(JsonParser.getString(r, "category", ""));
                    result.setTotalQuestions(JsonParser.getInt(r, "totalQuestions", 0));
                    result.setCorrect(JsonParser.getInt(r, "correct", 0));
                    result.setWrong(JsonParser.getInt(r, "wrong", 0));
                    result.setSkipped(JsonParser.getInt(r, "skipped", 0));
                    result.setScorePercent(JsonParser.getInt(r, "scorePercent", 0));
                    result.setDurationSeconds(JsonParser.getInt(r, "durationSeconds", 0));
                    result.setGrade(JsonParser.getString(r, "grade", "F"));
                    result.setTimestamp(JsonParser.getString(r, "timestamp", ""));
                    results.add(result);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading history: " + e.getMessage());
        }
        return results;
    }

    /**
     * Empties the signed-in user's quiz history (used by "Reset All Data").
     * Writes an empty list rather than deleting, so the legacy shared history file
     * can never be picked up again as a fallback.
     */
    public void clearHistory() {
        Path path = resolveQuizHistoryPath();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, "[]", StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error clearing history: " + e.getMessage());
        }
    }

    public void saveResult(QuizResult result) {
        Path path = resolveQuizHistoryPath();
        try {
            Files.createDirectories(path.getParent());

            List<QuizResult> existing = loadHistory();
            existing.add(0, result); // newest first

            if (existing.size() > 50) {
                existing = existing.subList(0, 50);
            }

            List<Object> arr = new ArrayList<>();
            for (QuizResult r : existing) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", r.getId());
                map.put("category", r.getCategory());
                map.put("totalQuestions", r.getTotalQuestions());
                map.put("correct", r.getCorrect());
                map.put("wrong", r.getWrong());
                map.put("skipped", r.getSkipped());
                map.put("scorePercent", r.getScorePercent());
                map.put("durationSeconds", r.getDurationSeconds());
                map.put("grade", r.getGrade());
                map.put("timestamp", r.getTimestamp());
                arr.add(map);
            }

            Files.writeString(path, JsonParser.toPrettyJson(arr), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error saving result: " + e.getMessage());
        }
    }
}
