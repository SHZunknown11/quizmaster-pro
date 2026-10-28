package com.quizmaster.view;

import com.quizmaster.model.Question;
import com.quizmaster.model.QuizResult;
import com.quizmaster.model.QuizSession;
import com.quizmaster.model.UserProfile;
import com.quizmaster.service.QuestionBankService;
import com.quizmaster.service.StorageService;
import com.quizmaster.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Main application window for QuizMaster Pro.
 * Assembles the Sidebar navigation rail on the West and a CardLayout container on the Center.
 */
public class MainWindow extends JFrame {

    public static final String CARD_LOGIN           = "LOGIN";
    public static final String CARD_DASHBOARD       = "DASHBOARD";
    public static final String CARD_CATEGORY_SELECT = "CATEGORY_SELECT";
    public static final String CARD_QUIZ            = "QUIZ";
    public static final String CARD_RESULT          = "RESULT";
    public static final String CARD_HISTORY         = "HISTORY";
    public static final String CARD_SETTINGS        = "SETTINGS";

    private final StorageService storageService;
    private final QuestionBankService questionBank;

    private SidebarNav sidebar;
    private CardLayout cardLayout;
    private JPanel cardContainer;

    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private CategorySelectPanel categorySelectPanel;
    private QuizPanel quizPanel;
    private ResultPanel resultPanel;
    private HistoryPanel historyPanel;
    private SettingsPanel settingsPanel;

    private String lastCategory = "Java Fundamentals";
    private int lastQuestionCount = 10;
    private QuizSession currentSession;

    public MainWindow(Path dataRoot) {
        super("QuizMaster Pro — Java Desktop Quiz Platform");

        this.storageService = new StorageService(dataRoot);
        this.questionBank = new QuestionBankService(dataRoot.resolve("questions"));

        initWindow();
        initViews();

        // Start on Login Screen (sidebar hidden until authenticated)
        sidebar.setVisible(false);
        showScreen(CARD_LOGIN);
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(AppTheme.WINDOW_WIDTH, AppTheme.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(920, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());
    }

    private void initViews() {
        // Sidebar Navigation
        sidebar = new SidebarNav(this::handleSidebarNavigation);
        add(sidebar, BorderLayout.WEST);

        // Center Card Container
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        // 0. Login & Registration
        loginPanel = new LoginPanel(storageService, username -> {
            sidebar.setVisible(true);
            sidebar.setActiveNav(SidebarNav.NAV_HOME);
            dashboardPanel.refreshData();
            settingsPanel.refreshProfile();
            showScreen(CARD_DASHBOARD);
            getContentPane().revalidate();
            getContentPane().repaint();
        });

        // 1. Dashboard
        dashboardPanel = new DashboardPanel(storageService, () -> {
            sidebar.setActiveNav(SidebarNav.NAV_QUIZ);
            showScreen(CARD_CATEGORY_SELECT);
        });

        // 2. Category Selection
        categorySelectPanel = new CategorySelectPanel(
                questionBank,
                this::startQuiz,
                () -> {
                    sidebar.setActiveNav(SidebarNav.NAV_HOME);
                    showScreen(CARD_DASHBOARD);
                }
        );

        // 3. Active Quiz Panel
        quizPanel = new QuizPanel(
                this::handleQuizFinished,
                () -> {
                    sidebar.setActiveNav(SidebarNav.NAV_HOME);
                    showScreen(CARD_DASHBOARD);
                }
        );

        // 4. Result Panel
        resultPanel = new ResultPanel(
                this::retryQuiz,
                () -> {
                    sidebar.setActiveNav(SidebarNav.NAV_QUIZ);
                    showScreen(CARD_CATEGORY_SELECT);
                }
        );

        // 5. History Panel
        historyPanel = new HistoryPanel(storageService, () -> {
            sidebar.setActiveNav(SidebarNav.NAV_HOME);
            showScreen(CARD_DASHBOARD);
        });

        // 6. Settings Panel
        settingsPanel = new SettingsPanel(
                storageService,
                () -> {
                    sidebar.setActiveNav(SidebarNav.NAV_HOME);
                    showScreen(CARD_DASHBOARD);
                },
                () -> dashboardPanel.refreshData(),
                this::handleLogout
        );

        cardContainer.add(loginPanel, CARD_LOGIN);
        cardContainer.add(dashboardPanel, CARD_DASHBOARD);
        cardContainer.add(categorySelectPanel, CARD_CATEGORY_SELECT);
        cardContainer.add(quizPanel, CARD_QUIZ);
        cardContainer.add(resultPanel, CARD_RESULT);
        cardContainer.add(historyPanel, CARD_HISTORY);
        cardContainer.add(settingsPanel, CARD_SETTINGS);

        add(cardContainer, BorderLayout.CENTER);
    }

    /**
     * Leaving a running quiz throws its progress away, so ask first. Returns true
     * if navigation may continue.
     */
    private boolean confirmLeaveQuiz() {
        if (!quizPanel.isQuizInProgress()) {
            return true;
        }
        quizPanel.pauseTimer();
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Leave the quiz? Your current progress will be lost.",
                "Leave Quiz",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            quizPanel.abandonSession();
            return true;
        }
        quizPanel.resumeTimer();
        return false;
    }

    private void handleSidebarNavigation(String navId) {
        if (!SidebarNav.NAV_LOGOUT.equals(navId) && !confirmLeaveQuiz()) {
            // the sidebar already moved its highlight on click: put it back on the quiz
            sidebar.setActiveNav(SidebarNav.NAV_QUIZ);
            return;
        }
        switch (navId) {
            case SidebarNav.NAV_HOME:
                dashboardPanel.refreshData();
                showScreen(CARD_DASHBOARD);
                break;
            case SidebarNav.NAV_QUIZ:
                showScreen(CARD_CATEGORY_SELECT);
                break;
            case SidebarNav.NAV_HISTORY:
                historyPanel.refreshHistory();
                showScreen(CARD_HISTORY);
                break;
            case SidebarNav.NAV_SETTINGS:
                settingsPanel.refreshProfile();
                showScreen(CARD_SETTINGS);
                break;
            case SidebarNav.NAV_LOGOUT:
                handleLogout();
                break;
        }
    }

    private void handleLogout() {
        boolean quizRunning = quizPanel.isQuizInProgress();
        if (quizRunning) {
            quizPanel.pauseTimer();
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out?",
                "Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            if (quizRunning) {
                quizPanel.resumeTimer();
            }
            return;
        }
        quizPanel.abandonSession();
        storageService.logout();
        sidebar.setVisible(false);
        loginPanel.resetForm();
        showScreen(CARD_LOGIN);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    public void showScreen(String cardName) {
        // BUG-10: clear any stale category highlight before the panel is shown
        if (CARD_CATEGORY_SELECT.equals(cardName) && categorySelectPanel != null) {
            categorySelectPanel.reset();
        }
        cardLayout.show(cardContainer, cardName);
    }

    private void startQuiz(String category, int count) {
        this.lastCategory = category;
        this.lastQuestionCount = count;

        List<Question> questions = questionBank.getShuffledQuestions(category, count);
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No questions found for " + category + "!\nPlease ensure question files are in data/questions.",
                    "Question Bank Empty",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int timerSecs = settingsPanel.getDefaultTimer();
        currentSession = new QuizSession(questions, category, timerSecs);
        // Show the panel first so QuizPanel's visibility guard (BUG-12) sees it as
        // showing when the per-question countdown starts.
        showScreen(CARD_QUIZ);
        quizPanel.startSession(currentSession);
    }

    private void retryQuiz() {
        startQuiz(lastCategory, lastQuestionCount);
    }

    private void handleQuizFinished(QuizResult result) {
        // Save result to disk
        storageService.saveResult(result);

        // Update user profile statistics using the correct recordQuiz() method
        // which handles streak logic, rolling average, and recent activity properly
        UserProfile profile = storageService.loadProfile();
        profile.recordQuiz(result.getCategory(), result.getScorePercent());

        storageService.saveProfile(profile);

        // Display results on result panel
        resultPanel.displayResult(result, currentSession);
        showScreen(CARD_RESULT);
    }
}
