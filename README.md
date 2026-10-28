# 🎓 QuizMaster Pro — Desktop Quiz Platform

A modern, high-performance **Java Swing Desktop Application** for testing and mastering core Java concepts. Designed with human-grade UI/UX aesthetics, custom anti-aliased vector rendering via `java.awt.Graphics2D`, procedural audio cues, and zero external dependency JSON persistence.

---

## 🌟 Key Features

0. **Authentication & Multi-User Accounts:**
   - **Login & Registration Screen (`LoginPanel.java`)**: Aesthetic centered dark slate card with `[Q]` emblem.
   - Tab switcher for **"Sign In"** vs **"Create Account"**.
   - Secure password storage using salted SHA-256 password hashing via standard `java.security.MessageDigest`.
   - **Guest Mode**: Instant one-click guest access without password friction.
   - **Multi-User Partitioning**: Each user maintains their own independent quizzes count, streak, best score, and history log.
   - **Sign Out / Switch User**: Accessible anytime via the bottom sidebar rail or settings panel.
   - Default demo account: `taha` / `taha123`.

1. **Dashboard (Screen 1 — The Dashboard):**
   - Dynamic time-of-day greeting (`Good morning, Taha`, `Good afternoon`, `Good evening`).
   - 2x2 KPI metrics display: **Total Quizzes**, **Best Score** (amber highlight), **Day Streak** (with vector flame badge), and **Average Score** (sky blue highlight).
   - Recent activity stream with status indicator dots, scores, and relative timestamps (`Just now`, `Yesterday`, `2 days ago`).
   - Wide primary amber CTA button (`Start New Quiz →`).

2. **Category & Topic Selection:**
   - 4 curated question banks:
     - **Java Fundamentals** (Keywords, primitives, control flow, methods, syntax)
     - **OOP Concepts** (Inheritance, encapsulation, polymorphism, abstraction)
     - **Collections Framework** (List, Set, Map, iterators, hashing, sorting)
     - **Data Structures** (Arrays, linked lists, trees, heaps, complexity)
   - Configurable question counts: **5**, **10**, or **15** questions per session.

3. **Active Quiz Engine (The Engine):**
   - Category breadcrumb with question counter (`Question 05 of 10`).
   - Smooth horizontal progress strip.
   - **Circular countdown timer** (30s default) with dynamic color warning and auto-advance on timeout.
   - **Lifelines:**
     - `⚡ 50:50 Lifeline`: Eliminates two incorrect options.
     - `💡 Hint`: Displays context-specific guidance.
   - **Interactive MCQ Option Cards [A]-[D]**: Hover glow, keyboard selection (`1`-`4` or `A`-`D`), tactile click displacement.
   - Immediate feedback: Correct answers turn green, mistakes turn red, with procedural audio chimes.
   - Detailed explanation card for every question.

4. **Quiz Results (Screen 2 — Quiz Complete!):**
   - **Animated Circular Score Meter** with percentage and sub-label (`"12 of 15 Correct"`).
   - Metric pill badges: `✓ Correct`, `✕ Wrong`, `→ Skipped`, and computed `Grade (A+, A, B, C, F)`.
   - Action buttons: `Try Again ↻` (reshuffles questions) and `New Quiz`.
   - **Review Answers Dialog**: Full modal breakdown of all questions, user's choices, correct answers, and explanations.

5. **History & Settings:**
   - Comprehensive quiz history tracking with timestamps, durations, and grades.
   - Settings panel with customizable display name, procedural audio toggle, and question timer duration.

6. **Zero External Dependencies:**
   - Pure standard Java (runs on standard JDK 17+ up to JDK 26).
   - Built-in zero-dependency JSON parser & serializer (`com.quizmaster.service.JsonParser`).
   - Built-in procedural sound generator using `javax.sound.sampled`.

---

## 🚀 How to Run

### Quick Start (Windows)
Double-click **`run.bat`** or run from PowerShell:
```powershell
.\run.bat
```

### Running the JAR Directly
```powershell
java -jar QuizMasterPro.jar
```

### Compiling from Source
Double-click **`build.bat`** or run:
```powershell
javac -d bin -sourcepath src src/com/quizmaster/Main.java
jar cfe QuizMasterPro.jar com.quizmaster.Main -C bin .
```

---

## 📂 Project Architecture

```
c:/Users/Taha/Desktop/java sem5/sem project java/
├── src/com/quizmaster/
│   ├── Main.java                      # Application entry point & LaF configuration
│   ├── model/
│   │   ├── Category.java              # Category model with metadata
│   │   ├── Question.java              # Immutable MCQ question model
│   │   ├── QuizResult.java            # Quiz completion record & grade calculator
│   │   ├── QuizSession.java           # Runtime quiz state, lifelines, scoring & timer
│   │   └── UserProfile.java           # User statistics & recent activity tracking
│   ├── components/
│   │   ├── CircularScoreMeter.java    # Animated circular score meter arc widget
│   │   ├── OptionCardButton.java      # MCQ choice card [A]-[D] with feedback states
│   │   ├── PillBadge.java             # Stadium/capsule status pill badge
│   │   ├── PrimaryButton.java         # Warm golden amber CTA button
│   │   ├── RoundedCard.java           # Anti-aliased rounded container panel
│   │   ├── SecondaryButton.java       # Outlined dark button with hover highlight
│   │   └── StatCard.java              # 2x2 KPI stat card with vector icons
│   ├── view/
│   │   ├── MainWindow.java            # Main window container with CardLayout & Sidebar
│   │   ├── SidebarNav.java            # Left vertical navigation rail (width: 72px)
│   │   ├── DashboardPanel.java        # Screen 1: Dashboard (Left Mockup)
│   │   ├── CategorySelectPanel.java   # Topic & question count selector
│   │   ├── QuizPanel.java             # Screen: Active quiz screen with timer & lifelines
│   │   ├── ResultPanel.java           # Screen 2: Quiz Complete (Right Mockup)
│   │   ├── ReviewAnswersDialog.java   # Question-by-question review modal
│   │   ├── HistoryPanel.java          # Past quiz attempts & records
│   │   └── SettingsPanel.java         # Audio toggle, timer, name & reset options
│   ├── service/
│   │   ├── AudioService.java          # Procedural sound synthesizer (javax.sound.sampled)
│   │   ├── JsonParser.java            # Zero-dependency JSON parser & serializer
│   │   ├── QuestionBankService.java   # Loads and shuffles question banks
│   │   └── StorageService.java        # Flat-file JSON persistence for profiles & history
│   └── util/
│       └── AppTheme.java              # Design tokens: palette, fonts, strokes & rendering hints
├── data/
│   ├── questions/                     # JSON question banks
│   │   ├── java_fundamentals.json
│   │   ├── oop_concepts.json
│   │   ├── collections_framework.json
│   │   └── data_structures.json
│   ├── users/
│   │   └── user_profile.json          # User statistics persistence
│   └── history/
│       └── quiz_history.json          # Quiz attempt history persistence
├── build.bat                          # Automated build and package script
├── run.bat                            # Launch script
└── QuizMasterPro.jar                  # Standalone executable JAR
```

---

## 🎨 Design System Tokens

- **Canvas Background**: Deep Midnight Slate (`#0F172A`)
- **Card Surface**: Dark Slate (`#1E293B`)
- **Hover Surface**: Elevated Slate (`#27354A`)
- **Sidebar Surface**: Deepest Slate (`#0B0F17`)
- **Primary Accent**: Warm Golden Amber (`#F59E0B`)
- **Success / Correct**: Emerald Green (`#10B981`)
- **Error / Incorrect**: Crimson Red (`#EF4444`)
- **Secondary Accent**: Sky Cyan (`#38BDF8`)
- **Typography**: Segoe UI (Body & Headings) + Consolas (Code blocks)
