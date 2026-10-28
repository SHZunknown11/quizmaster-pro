# 🎓 QuizMaster Pro — Implementation Plan & UI Architecture

> **Execution Notice:** This implementation plan is specifically tailored for **QuizMaster Pro (Java Swing Desktop Application)**, directly implementing the user's Dashboard and Quiz Result mockup screens with polished, human-grade UI/UX enhancements. Follow this plan task-by-task.

**Goal:** Build a complete, highly-polished, OOP-compliant Java Swing desktop MCQ Quiz Application with a custom dark slate & warm amber theme, custom-painted vector UI components (cards, circular score ring, sidebar rail), local JSON persistence, multi-category question banks, sound effects, and smooth animations.

**Architecture:** Model-View-Controller (MVC) + Component-based custom Swing rendering (`Graphics2D` anti-aliasing). Single-window container using `CardLayout` for seamless screen transitions without flickering. Zero external Maven/Gradle heavyweight dependencies required (runs out-of-the-box on Java 17+ up to Java 26 with standard JDK libraries).

**Tech Stack:** 
- Language: Java SE 17+ (JDK 26 tested)
- GUI: Java Swing + `java.awt.Graphics2D` (custom painting, shapes, arcs, gradients)
- Storage: Flat-file JSON (`data/questions/*.json`, `data/users/users.json`, `data/history/history.json`)
- Audio: `javax.sound.sampled` for subtle UI interaction audio cues

---

## 🎨 Design System & UI/UX Improvements (Anti-Slop Audit)

Based on the uploaded mockups, the following design improvements are integrated:

### 1. Typography & Layout Corrections
| Mockup Element | Issue in Mockup | Engineered Improvement in Plan |
| :--- | :--- | :--- |
| **Score Ring Center** | Glitchy text `"in 24px"` inside score ring | Replaced with semantic sub-metric: `"12 of 15 Correct"` or `"Score: 800 pts"` with clean font scaling. |
| **Greeting & Streak** | Raw emojis (`👋`, `🔥`) which render inconsistently across OS | Custom anti-aliased Java 2D vector icons (crisp flame badge, verified checkmark, sleek chevron arrows). |
| **Recent Activity List** | Identical duplicated entries (`"OOP Concepts 85% 2 days ago"`) | Dynamic activity stream with varied subjects, dynamic status badges, and relative timestamps (`"Just now"`, `"Yesterday"`, `"3 days ago"`). |
| **Button States** | Static appearance without feedback | 8-State interactive system (`Default`, `Hover` with brightness filter, `Pressed` with 2px optical inset, `Focused` with crisp border ring). |

### 2. Refined Color Tokens
```java
public class AppTheme {
    // Canvas & Surfaces
    public static final Color BG_CANVAS        = new Color(0x0F, 0x17, 0x2A); // #0F172A Deep Midnight Slate
    public static final Color BG_CARD          = new Color(0x1E, 0x29, 0x3B); // #1E293B Card Background
    public static final Color BG_CARD_HOVER    = new Color(0x27, 0x35, 0x4A); // #27354A Elevated Hover
    public static final Color BG_SURFACE_DARK  = new Color(0x0B, 0x0F, 0x17); // #0B0F17 Deepest Sidebar
    public static final Color BORDER_SUBTLE     = new Color(0x33, 0x41, 0x55); // #334155 1px Hairline Border
    public static final Color BORDER_ACTIVE     = new Color(0xF5, 0x9E, 0x0B); // #F59E0B Active Amber Border

    // Primary Accents
    public static final Color ACCENT_AMBER     = new Color(0xF5, 0x9E, 0x0B); // #F59E0B Warm Golden Amber
    public static final Color ACCENT_AMBER_HOVER = new Color(0xD9, 0x77, 0x06); // #D97706
    public static final Color ACCENT_BLUE      = new Color(0x38, 0xBD, 0xF8); // #38BDF8 Sky Cyan
    public static final Color ACCENT_GREEN     = new Color(0x10, 0xB9, 0x81); // #10B981 Emerald Correct
    public static final Color ACCENT_RED       = new Color(0xEF, 0x44, 0x44); // #EF4444 Crimson Incorrect
    public static final Color ACCENT_SLATE     = new Color(0x64, 0x74, 0x8B); // #64748B Muted Skipped

    // Text Hierarchy
    public static final Color TEXT_PRIMARY     = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC Pure Crisp White
    public static final Color TEXT_SECONDARY   = new Color(0x94, 0xA3, 0xB8); // #94A3B8 Cool Muted Slate
    public static final Color TEXT_MUTED       = new Color(0x64, 0x74, 0x8B); // #64748B Low Priority

    // Standard Fonts
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADING      = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY         = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_CODE         = new Font("Consolas", Font.PLAIN, 13);
    public static final Font FONT_KPI_NUM      = new Font("Segoe UI", Font.BOLD, 28);
}
```

---

## 🏛️ Codebase Structure

```
c:/Users/Taha/Desktop/java sem5/sem project java/
├── src/
│   └── com/quizmaster/
│       ├── Main.java                          ← Entry point, LaF config & window init
│       ├── model/
│       │   ├── Question.java                  ← Question model (ID, prompt, options, correctIdx, hint)
│       │   ├── QuizSession.java               ← In-memory quiz runtime state, timer & scoring
│       │   ├── UserProfile.java               ← User stats, streaks, best/average scores
│       │   ├── QuizResult.java                ← Finished quiz record (score, duration, breakdown)
│       │   └── Category.java                  ← Category enum / definition
│       ├── view/
│       │   ├── MainWindow.java                ← JFrame container with CardLayout & Sidebar
│       │   ├── SidebarNav.java                ← Left icon-rail navigation (Home, Quiz, History, Settings)
│       │   ├── DashboardPanel.java            ← Screen 1: The exact Left Mockup
│       │   ├── CategorySelectPanel.java       ← Topic & difficulty picker
│       │   ├── QuizPanel.java                 ← Active quiz screen with timer, question & options
│       │   ├── ResultPanel.java               ← Screen 2: The exact Right Mockup with Score Ring
│       │   ├── ReviewAnswersDialog.java       ← Detailed question-by-question review dialog
│       │   ├── HistoryPanel.java              ← Past quiz attempts & leaderboard
│       │   └── SettingsPanel.java             ← Audio toggle, timer duration, reset data
│       ├── components/
│       │   ├── RoundedCard.java               ← Custom rounded container with subtle border
│       │   ├── StatCard.java                  ← 2x2 KPI card widget (Number + Label + Accent)
│       │   ├── CircularScoreMeter.java        ← Smooth antialiased circular score ring widget
│       │   ├── PillBadge.java                 ← Status pill (Correct, Wrong, Skipped, Grade)
│       │   ├── PrimaryButton.java             ← Amber rounded CTA with hover & press physics
│       │   ├── SecondaryButton.java           ← Dark outlined button with border highlight
│       │   └── OptionCardButton.java          ← MCQ choice card with shortcut keys [A]-[D]
│       ├── service/
│       │   ├── QuestionBankService.java       ← Loads & shuffles questions from JSON
│       │   ├── StorageService.java            ← Reads/writes UserProfile and QuizResult files
│       │   ├── AudioService.java              ← Procedural or WAV audio playback
│       │   └── JsonParser.java                ← Lightweight standalone JSON parser (zero-dep)
│       └── util/
│           └── AppTheme.java                  ← Colors, fonts, metrics & rendering utilities
├── data/
│   ├── questions/
│   │   ├── java_fundamentals.json
│   │   ├── oop_concepts.json
│   │   ├── collections_framework.json
│   │   └── data_structures.json
│   ├── users/
│   │   └── user_profile.json
│   └── history/
│       └── quiz_history.json
├── docs/
│   └── plans/
│       └── 2026-10-27-quizmaster-pro-ui.md    ← This specification
└── README.md
```

---

## 📋 Phased Implementation Plan

### Phase 1: Foundation, Data Models & Zero-Dependency JSON Engine
- [x] **Task 1.1: Core Theme & Utilities**
  - Create `com.quizmaster.util.AppTheme` with color palette, font definitions, rendering hints helper (`applyQualityRenderingHints(Graphics2D g2)`).
- [x] **Task 1.2: Lightweight JSON Parser & Serializer**
  - Create `com.quizmaster.service.JsonParser` to parse and serialize simple JSON objects/arrays without requiring external JARs.
- [x] **Task 1.3: Data Models**
  - Implement `Question.java` (immutable question data).
  - Implement `UserProfile.java` (stores `totalQuizzes`, `bestScore`, `dayStreak`, `averageScore`, `recentActivities`).
  - Implement `QuizResult.java` (stores `quizId`, `category`, `scorePercent`, `correctCount`, `wrongCount`, `skippedCount`, `durationSeconds`, `timestamp`, `grade`).
  - Implement `QuizSession.java` (handles session logic: current question index, selected answers, lifeline state, timer countdown).
- [x] **Task 1.4: Pre-loaded Question Bank JSON Files**
  - Create `data/questions/java_fundamentals.json` (15+ curated questions).
  - Create `data/questions/oop_concepts.json` (15+ curated questions).
  - Create `data/questions/collections_framework.json` (15+ curated questions).
  - Create `data/questions/data_structures.json` (15+ curated questions).
  - Create default `data/users/user_profile.json` (matching initial user state: Taha, 14 quizzes, 92% best score, 5-day streak, 78% avg score).

---

### Phase 2: Custom Vector UI Components (Anti-Slop Craft)
- [x] **Task 2.1: `RoundedCard.java`**
  - Extends `JPanel`.
  - Overrides `paintComponent(Graphics g)`: anti-aliased `fillRoundRect` and `drawRoundRect`.
  - Configurable corner radius (default 16px), background color, border color, and drop padding.
- [x] **Task 2.2: `StatCard.java` (The 2x2 KPI Cards)**
  - Extends `RoundedCard`.
  - Displays large KPI number (`setFont(FONT_KPI_NUM)`), title label (`Total Quizzes`), and optional vector badge (e.g. flame for Day Streak).
- [x] **Task 2.3: `CircularScoreMeter.java` (Right Mockup Hero Widget)**
  - Extends `JComponent`.
  - Overrides `paintComponent(Graphics g)`.
  - Draws a dark background arc track (`#27354A`) and an active amber arc (`#F59E0B`) proportional to `scorePercent`.
  - Draws centered text: `"80%"` in 36px Bold + subtitle `"12 of 15 Correct"` in 13px Regular (eliminating the `"in 24px"` glitch).
  - Supports smooth animated fill on appearance via `javax.swing.Timer`.
- [x] **Task 2.4: `PillBadge.java`**
  - Rounded pill chip displaying status (e.g., `✓ 8 Correct` in green, `✕ 1 Wrong` in red, `→ 1 Skipped` in slate, `Grade A` in amber/blue).
- [x] **Task 2.5: `PrimaryButton.java` & `SecondaryButton.java`**
  - Amber rounded primary CTA (`Start New Quiz →`, `Try Again`).
  - Hover brightness animation (`MouseAdapter`), `:active` 1px downward translation for tactile feel.
  - Secondary outlined button (`New Quiz`).
- [x] **Task 2.6: `OptionCardButton.java` (MCQ Choices)**
  - Choice card with letter indicator `[A]`, `[B]`, `[C]`, `[D]`, clean question option text, and keyboard indicator.
  - States: Default, Hover (border highlight), Selected (amber border + subtle amber tint), Correct (green), Wrong (red).

---

### Phase 3: Screen 1 — The Dashboard (Left Mockup)
- [x] **Task 3.1: Sidebar Navigation Rail (`SidebarNav.java`)**
  - Left vertical column (width: 72px, background: `#0B0F17`).
  - Top menu toggle button [☰].
  - Navigation icons:
    - `[⌂ Home]`
    - `[📝 Quizzes]` (active indicator with amber rounded pill background as shown in mockup)
    - `[📅 History]`
    - `[⚙ Settings]`
  - Smooth card-switching callbacks to `MainWindow`.
- [x] **Task 3.2: Welcome Header & 2x2 Stats Grid (`DashboardPanel.java`)**
  - Header: `"Good morning, Taha"` (with time-of-day greeting generator: morning/afternoon/evening) + subtitle `"Ready for today's challenge?"`.
  - 2x2 Grid of `StatCard` instances:
    1. Total Quizzes: `14`
    2. Best Score: `92%` (amber highlighted)
    3. Day Streak: `5` (with clean vector flame icon)
    4. Average Score: `78%` (sky blue highlighted)
- [x] **Task 3.3: Recent Activity Section**
  - Header label `"Recent Activity"` in warm amber.
  - Vertical list of 3 rounded cards:
    - Status indicator dot (green/red/blue)
    - Category title (`OOP Concepts`, `Java Fundamentals`, `Collections`)
    - Score badge pill (`85%`, `90%`, `75%`)
    - Relative timestamp (`2 days ago`, `Yesterday`, `Just now`)
- [x] **Task 3.4: Main Action Bar**
  - Wide `PrimaryButton` at bottom: `"Start New Quiz →"`.
  - On click, triggers transition to category picker or immediate quiz start.

---

### Phase 4: Active Quiz Screen (The Engine)
- [x] **Task 4.1: Quiz Header & Countdown Timer (`QuizPanel.java`)**
  - Breadcrumb: `"Java Fundamentals / Question 05 of 15"`.
  - Horizontal smooth progress bar strip.
  - Circular countdown timer badge (30-second countdown with visual arc shrink; auto-submits on timeout).
- [x] **Task 4.2: Question Card & Code Block Container**
  - Large readable question text card.
  - Support for multi-line Java code snippet box with dark contrast syntax styling.
- [x] **Task 4.3: 4 MCQ Option Cards & Lifelines**
  - 4 `OptionCardButton` components laid out vertically.
  - Keyboard listeners: Pressing keys `1`, `2`, `3`, `4` or `A`, `B`, `C`, `D` selects options immediately.
  - Lifeline bar:
    - `[50:50 Lifeline]` (disables two incorrect options)
    - `[Hint]` (reveals subtle guidance card)
- [x] **Task 4.4: Footer Navigation**
  - `[Skip Question]` secondary button.
  - `[Confirm Answer →]` primary amber button.
  - Immediate audio feedback: subtle clean chime on correct, soft thud on incorrect.

---

### Phase 5: Screen 2 — Quiz Complete! (Right Mockup)
- [x] **Task 5.1: Results Header & Circular Score Arc (`ResultPanel.java`)**
  - Top title: `"Quiz Complete!"` in crisp white 26px Bold.
  - Centered `CircularScoreMeter` showing percentage (e.g. `80%`) with subtitle `"12 of 15 Correct"`.
- [x] **Task 5.2: Metric Pills & Grade Badge**
  - Horizontal pill row:
    - Green pill: `✓ 8 Correct`
    - Red pill: `✕ 1 Wrong`
    - Slate pill: `→ 1 Skipped`
  - Large prominent grade badge: `"Grade A"` (calculated dynamically: A+ for ≥95%, A for ≥80%, B for ≥65%, C for ≥50%, F below).
  - Category & time metadata line: `"Java Fundamentals · 4m 32s"`.
- [x] **Task 5.3: Action Buttons & Answer Review Link**
  - Horizontal action pair:
    - `[Try Again]` (Primary Amber Button) — restarts the quiz with newly randomized questions.
    - `[New Quiz]` (Secondary Outlined Button) — returns to category selection.
  - Bottom text link: `"Review Answers →"` with hover underline.
- [x] **Task 5.4: Detailed Review Dialog (`ReviewAnswersDialog.java`)**
  - Modal dialog showing all questions from the completed quiz.
  - Displays user's selected choice vs the correct choice with concise explanation text.

---

### Phase 6: Storage, State Persistence & Main Application
- [x] **Task 6.1: `StorageService.java`**
  - Saves completed quiz results to `data/history/quiz_history.json`.
  - Recalculates user profile stats (`totalQuizzes++`, updates `bestScore`, updates `averageScore`, calculates streak) and saves to `data/users/user_profile.json`.
- [x] **Task 6.2: `MainWindow.java` & `Main.java`**
  - Sets system window decorations, minimum size `960x680`, non-resizable or smoothly responsive.
  - Assembles `SidebarNav` (West) and `CardLayout` container (Center).
  - Registers keyboard shortcuts (Ctrl+Q quit, Space/Enter to advance).
- [x] **Task 6.3: Build Scripts & Execution Testing**
  - Compile script: `javac -d bin -sourcepath src src/com/quizmaster/Main.java`
  - Launch script: `java -cp bin com.quizmaster.Main`
  - Standalone JAR packager: `jar cfe QuizMasterPro.jar com.quizmaster.Main -C bin .`

---

## 🎯 Verification & Acceptance Checklist

| Item | Acceptance Criteria |
| :--- | :--- |
| **Visual Accuracy** | Matches the dark slate (`#0F172A`) & warm amber (`#F59E0B`) layout from the mockup images. |
| **No "Slop" Glitches** | No `"in 24px"` text artifact; proper vector graphics instead of generic emojis; crisp 1px borders. |
| **Interactive Polish** | Buttons provide visual click and hover states; option selection highlights cleanly; smooth score meter arc. |
| **Full Flow** | Dashboard → Start Quiz → Answer Questions with Timer → Results Screen with Score Ring → Review Answers → Home. |
| **Zero External Deps** | Compiles with pure standard `javac` on Java 17/21/26 without requiring Gradle or Maven downloads. |
| **Data Persistence** | Stats and history persist between app restarts in the `data/` folder. |

---

> **Handoff:** When you switch to Claude Opus 4.5 / 5.5, begin directly at **Phase 1: Task 1.1** to construct the files sequentially.
