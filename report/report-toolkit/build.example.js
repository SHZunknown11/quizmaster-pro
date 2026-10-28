const fs = require('fs');
const path = require('path');
const L = require('./lib');
const {
  D, FONT, TEXTW, reg, runs, P, Pc, blank, chapter, H2, H3, plainChapter, frontHeading, bullet, numbered, code,
  figure, table, cell, tocLine, borders, noBorders, none, HERE,
} = L;
const {
  Document, Packer, Paragraph, TextRun, ImageRun, Table, TableRow, TableCell, WidthType, AlignmentType, Footer,
  PageNumber, NumberFormat, LevelFormat, SectionType, VerticalAlign, ShadingType, BorderStyle,
} = D;

const pagesFile = path.join(HERE, 'pages.json');
const pages = fs.existsSync(pagesFile) ? JSON.parse(fs.readFileSync(pagesFile, 'utf8')) : {};
const pg = (k) => pages[k] || '0';

// ───────────────────────── project facts (edit here if anything changes) ─────────────────────────
const TITLE = 'QUIZMASTER PRO: A JAVA SWING DESKTOP QUIZ APPLICATION';
const TITLE_CASE = 'QuizMaster Pro: A Java Swing Desktop Quiz Application';
const NAMES = [['MOHAMMAD TAHA', '24BCS12733'], ['SARWAR ADHIKARI', '24BCS80370']];
const SUPERVISOR = 'Mr. Shariq Rashid Bhat';
const SUBJECT = 'Project Based Learning in Java (24CSP-301 / 24ITP-301)';
const MONTH_YEAR = 'Oct-2026';
const ADDRESS = 'Chandigarh University, NH-05, Chandigarh-Ludhiana Highway, Mohali, Punjab - 140413';
const S = (n) => path.join('shots', n);

// ═════════════════════════ GRAPHICAL ABSTRACT FIGURE (Figure 1, front matter) ═════════════════════════
const graphicalFig = figure('fig_graphical.png', 'Graphical abstract of QuizMaster Pro', 540, { front: true });

// ═════════════════════════ CHAPTER 1 ═════════════════════════
const ch1 = [
  ...chapter(1, 'Introduction'),
  H2('1.1.', 'Client Identification / Need Identification / Identification of Relevant Contemporary Issue'),
  P('Learning to program is a skill that is built through repeated, active practice. Reading a textbook or watching a lecture on Java helps a student to understand a concept, but the knowledge only becomes dependable when the student is asked to recall it, apply it to a short piece of code and receive immediate feedback on whether the answer was right. Multiple-choice quizzes are one of the simplest and most widely used ways of providing this kind of practice. They are quick to attempt, they can be scored automatically and they allow a large part of a syllabus to be revised in a short time.'),
  P('The **client** for this project is the typical undergraduate computer-science student who is learning Java in the Project Based Learning course, together with the faculty who guide such a course and would like their students to revise core topics regularly. The **need** was identified from the way students currently prepare for lab evaluations and viva examinations: they search for scattered question lists, use online quiz websites that require an internet connection, an account and often show advertisements, or simply re-read notes without any way of checking what they have actually retained.'),
  P('The **contemporary issue** is the rapid shift towards self-paced, digital learning. Students increasingly expect immediate feedback, visible progress and a user experience comparable to the mobile and web applications they use every day. At the same time, many campus laboratories and hostel networks have limited or unreliable connectivity, and handing personal study data to third-party websites raises privacy concerns. A lightweight application that runs entirely on the student\'s own computer, stores its data locally and still offers a modern interface addresses all three concerns. [[Optional: add one or two cited statistics on e-learning / online assessment adoption here, if your faculty expects numerical evidence.]]'),
  H2('1.2.', 'Identification of Problem'),
  P('Students of Java do not have a free, offline and distraction-free way of practising the core topics of the language and tracking how their understanding improves over time. Existing options are either general-purpose quiz platforms that are not focused on Java, tools that depend on a constant internet connection and a registered online account, or static question lists that provide no scoring, no explanation of the correct answer and no record of past attempts. As a result, learners cannot easily identify which topics are weak, cannot measure improvement from one attempt to the next, and lose motivation because there is no feedback loop.'),
  H2('1.3.', 'Identification of Tasks'),
  P('To resolve the problem, the work was divided into the following tasks, which also form the framework of this report:'),
  ...numbered([
    '**Requirement analysis** - study similar quiz tools, list the features students need and decide the scope for a one-semester project.',
    '**Design** - choose the technology, define the software architecture, the data formats and the visual design language of the application.',
    '**Implementation of the data layer** - question banks, user accounts with secure password storage, profile statistics and quiz history.',
    '**Implementation of the quiz engine** - question selection and shuffling, per-question timer, scoring, lifelines (50:50 and hint) and result computation.',
    '**Implementation of the user interface** - login, dashboard, category selection, quiz, result, history, settings and answer-review screens.',
    '**Testing and validation** - automated logic tests, end-to-end navigation tests and visual inspection of every screen; defect fixing.',
    '**Documentation** - this report, a user manual and the project repository.',
  ]),
  H2('1.4.', 'Timeline'),
  P('The project was planned over eleven weeks. Table 1 shows the tasks of Section 1.3 as a Gantt chart; a shaded cell means that the task was active in that week. The weeks are indicative and may be adjusted to the actual dates of the project.', { keepNext: true }),
  ...(() => {
    const weeks = 11;
    const act = [
      ['Requirement analysis', 1, 2], ['Design (architecture, data, UI)', 2, 4], ['Data layer', 3, 5],
      ['Quiz engine', 4, 7], ['User interface', 5, 9], ['Testing and fixing', 8, 10], ['Documentation', 9, 11],
    ];
    const wNum = 380, wName = TEXTW - weeks * wNum;
    const widths = [wName, ...Array(weeks).fill(wNum)];
    const headers = ['Task', ...Array.from({ length: weeks }, (_, i) => String(i + 1))];
    const rows = act.map(([n, a, b]) => [n, ...Array.from({ length: weeks }, (_, i) => (i + 1 >= a && i + 1 <= b ? '#' : ''))]);
    return table('Project timeline (Gantt chart; columns are week numbers)', headers, rows, widths, {
      keep: true, shade: (r, c, v) => (v === '#' ? '7F7F7F' : null),
      align: [AlignmentType.LEFT, ...Array(weeks).fill(AlignmentType.CENTER)],
    }).map((x) => x);
  })(),
  H2('1.5.', 'Organization of the Report'),
  P('The remainder of the report is organised as follows:'),
  bullet('**Chapter 1 (Introduction)** identifies the need, the problem and the tasks, and gives the timeline.'),
  bullet('**Chapter 2 (Design Flow / Process)** evaluates features, lists the design constraints, compares three alternative designs, justifies the selected design and presents the implementation plan with flowchart, architecture and algorithms.'),
  bullet('**Chapter 3 (Results Analysis and Validation)** describes the tools used, shows the implemented screens, and reports the testing, the defects found and the validation results.'),
  bullet('**Chapter 4 (Conclusion and Future Work)** summarises the outcome, discusses deviations from the expected results and suggests further improvements.'),
  bullet('The **References**, the **Appendices** (data formats, source excerpts and the complete test list), the **User Manual** and the **Achievements** follow the main chapters.'),
];

// ═════════════════════════ CHAPTER 2 ═════════════════════════
const featureRows = [
  ['F1', 'Secure sign-in, registration and guest mode', 'Must', 'Selected'],
  ['F2', 'Multiple topic categories (Java fundamentals, OOP, collections, data structures)', 'Must', 'Selected'],
  ['F3', 'Multiple-choice questions with exactly four options and a stored correct answer', 'Must', 'Selected'],
  ['F4', 'Instant right / wrong feedback with an explanation', 'Must', 'Selected'],
  ['F5', 'Score, grade and breakdown (correct, wrong, skipped) at the end of a quiz', 'Must', 'Selected'],
  ['F6', 'Per-question countdown timer', 'Should', 'Selected'],
  ['F7', 'Lifelines: 50:50 and hint', 'Should', 'Selected'],
  ['F8', 'Quiz history, statistics, day streak and recent-activity feed', 'Should', 'Selected'],
  ['F9', 'Review of all answers after the quiz', 'Should', 'Selected'],
  ['F10', 'Settings: display name, timer length, sound on/off, data reset', 'Could', 'Selected'],
  ['F11', 'Keyboard shortcuts for answering', 'Could', 'Selected'],
  ['F12', 'Online multiplayer / live leaderboard', 'Could', 'Rejected (needs a server)'],
  ['F13', 'Database server for storage', 'Could', 'Rejected (setup cost)'],
  ['F14', 'In-app question editor', 'Could', 'Deferred to future work'],
];

const ch2 = [
  ...chapter(2, 'Design Flow / Process'),
  H2('2.1.', 'Evaluation and Selection of Specifications / Features'),
  P('Before design began, similar tools were reviewed: console-based Java quiz programs that are commonly written as beginner exercises, general quiz platforms such as classroom-response and form-based quiz services, and mobile learning applications. From this review a list of candidate features was prepared and each feature was rated as Must, Should or Could according to how directly it solves the problem of Section 1.2 and how much effort it needs. Table 2 shows the result.'),
  ...table('Candidate features and their selection', ['ID', 'Feature', 'Priority', 'Decision'], featureRows,
    [600, 4500, 1000, 2566], { align: [AlignmentType.CENTER, AlignmentType.LEFT, AlignmentType.CENTER, AlignmentType.LEFT] }),
  H2('2.2.', 'Design Constraints'),
  P('The design was shaped by the constraints listed in Table 3. Constraints that are not relevant to a desktop software project are noted as such rather than ignored.'),
  ...table('Design constraints considered', ['Constraint', 'How it was considered in the design'], [
    ['Regulations / standards', 'Only the standard Java platform API is used, which keeps the project free of licensing issues. JSON files follow the ECMA-404 syntax [3]. Passwords are never stored in plain text, in line with the guidance on memorised secrets in NIST SP 800-63B [5].'],
    ['Economic / cost', 'The whole stack (JDK, Swing, Git, an ordinary editor) is free. No server, database licence or hosting is needed, so the cost of building and running the application is zero.'],
    ['Environmental', 'The application is a small local program with no network traffic and no server infrastructure, so its energy footprint is low. The dark interface also draws less power on OLED displays.'],
    ['Health', 'A dark, low-glare colour scheme with high-contrast text reduces eye strain during long study sessions; text and background combinations were chosen with the contrast ideas of WCAG 2.1 in mind [10]. The timer length is configurable (15-60 s) so that time pressure can be reduced, and sound can be switched off.'],
    ['Manufacturability / portability', 'For software this means that it must build and run easily on another machine. A single batch file builds the project into one executable JAR, and the application only needs a JDK 17 or newer.'],
    ['Safety / security', 'Passwords are salted with 16 random bytes and hashed with SHA-256. User names are restricted to letters, digits, dot, dash and underscore because they become part of file names, which prevents invalid or malicious path characters. All data stays on the user\'s computer.'],
    ['Professional', 'Code follows a layered (MVC-style) structure [6], uses meaningful names and Javadoc comments, and is versioned in Git [2]. The design takes account of known good practice for Java [1] and for user-interface usability [7].'],
    ['Ethical', 'Scores are computed honestly and transparently; the application collects no personal data beyond a display name, shows no advertisements and does not track the user. Any AI assistance used in development is disclosed in Section 3.1.'],
    ['Social', 'Keyboard shortcuts, readable font sizes, colour plus text/icon cues for right and wrong answers (not colour alone) and an offline mode make the tool usable by a wider group of students.'],
    ['Political', 'No political constraint applies. The question content is purely technical and neutral.'],
  ], [2000, 6666]),
  H2('2.3.', 'Analysis and Feature Finalization Subject to Constraints'),
  P('Applying the constraints of Section 2.2 to the features of Table 2 led to the following changes:'),
  bullet('**Removed:** online multiplayer and live leaderboard (F12) - they would need a server, an internet connection and the storage of other people\'s data, which conflicts with the cost, privacy and portability constraints. A database server (F13) was also removed for the same reason.'),
  bullet('**Modified:** persistence was changed from a database to human-readable JSON files read by a small custom parser. The 50:50 and hint lifelines were limited to **one use per quiz** so that they stay a strategic choice, and hints are disabled automatically on a question that has none.'),
  bullet('**Added:** a guest mode (no registration needed), a day-streak counter and recent-activity feed to encourage regular practice, a confirmation before leaving a running quiz, keyboard shortcuts and a sound on/off switch.'),
  bullet('**Deferred:** the in-app question editor (F14) is left for future work; questions are currently edited as JSON files.'),
  H2('2.4.', 'Design Flow'),
  P('Three alternative designs were prepared (Figure 2). All three give the student the same quiz functionality; they differ in the user-interface technology and in how data are stored.'),
  ...figure('fig_designs.png', 'Block diagrams of the three alternative designs', 540),
  H3('2.4.1.', 'Design A - Console application with text files'),
  P('A command-line program that prints a question, reads the choice through the Scanner class and stores results in plain text files. It is the quickest design to write and has no graphical dependencies, but it offers no visual feedback, cannot show a countdown ring or score meter, has poor discoverability and is unattractive for repeated use.'),
  H3('2.4.2.', 'Design B - Swing desktop GUI with JSON files'),
  P('A single-window Swing application. Screens are Swing panels switched with a CardLayout; custom components are painted with Java 2D so that the application has a modern, consistent look. Data are stored as JSON files in a data folder. It needs only the JDK, runs offline and gives rich visual feedback.'),
  H3('2.4.3.', 'Design C - JavaFX GUI with a database'),
  P('A JavaFX interface described in FXML, with a data-access layer over MySQL or SQLite. This design is the most scalable and would allow queries over large amounts of data, but JavaFX is no longer bundled with the JDK, the database must be installed and configured, and the extra layers are too heavy for a one-semester project.'),
  H2('2.5.', 'Design Selection'),
  P('The designs were compared using five weighted criteria. Each criterion was given a score from 1 (poor) to 5 (excellent); the weighted score is the sum of weight x score divided by 100. Table 4 shows the comparison.'),
  ...(() => {
    const crit = [
      ['User experience and visual feedback', 30, 2, 5, 5],
      ['Development effort (fits the semester)', 20, 5, 4, 2],
      ['Set-up and portability (zero install)', 20, 5, 5, 2],
      ['Data handling and scalability', 15, 1, 3, 5],
      ['Extensibility and maintainability', 15, 2, 4, 4],
    ];
    const tot = [0, 0, 0];
    crit.forEach((c) => { for (let i = 0; i < 3; i++) tot[i] += c[1] * c[2 + i] / 100; });
    const rows = crit.map((c) => [c[0], String(c[1]) + '%', String(c[2]), String(c[3]), String(c[4])]);
    rows.push(['**Weighted score (out of 5)**', '**100%**', `**${tot[0].toFixed(2)}**`, `**${tot[1].toFixed(2)}**`, `**${tot[2].toFixed(2)}**`]);
    return table('Weighted comparison of the alternative designs', ['Criterion', 'Weight', 'Design A', 'Design B', 'Design C'], rows,
      [3466, 1000, 1400, 1400, 1400], {
        keep: true,
        align: [AlignmentType.LEFT, AlignmentType.CENTER, AlignmentType.CENTER, AlignmentType.CENTER, AlignmentType.CENTER],
        shade: (r, c) => (c === 3 ? 'EDEDED' : null),
      });
  })(),
  P('**Design B scores highest** and was selected. Its main advantages are the rich interface that makes the practice engaging, the fact that it needs nothing except the JDK, and an architecture that is simple enough to complete and test within the semester. Its main weakness - flat files do not scale to very large data sets - is acceptable because the data of a single student (a few thousand records at most) are small, and the storage code is isolated in one service class that can later be replaced by a database (Chapter 4).'),
  H2('2.6.', 'Implementation Plan / Methodology'),
  H3('2.6.1.', 'Architecture'),
  P('The application uses a layered, MVC-style architecture (Figure 3). The **presentation layer** contains the window, the screens and reusable custom components; the **model layer** holds plain Java classes for questions, sessions, results and users; the **service layer** reads and writes files, parses JSON and plays sounds. Views never touch files directly; they ask a service. Table 5 lists the source packages.'),
  ...figure('fig_architecture.png', 'Layered architecture of QuizMaster Pro', 520),
  ...table('Source packages of the project', ['Package', 'Files', 'Lines', 'Responsibility'], [
    ['com.quizmaster.view', '10', '3,334', 'Main window, sidebar navigation and the seven screens / dialogs'],
    ['com.quizmaster.components', '8', '1,174', 'Custom-painted reusable widgets (cards, buttons, badges, score ring, input field)'],
    ['com.quizmaster.model', '6', '563', 'Question, QuizSession, QuizResult, UserAccount, UserProfile, Category'],
    ['com.quizmaster.service', '4', '938', 'StorageService, QuestionBankService, JsonParser, AudioService'],
    ['com.quizmaster.util', '4', '283', 'AppTheme (design tokens), HtmlText, DarkScrollBarUI, UiKit'],
    ['com.quizmaster', '1', '54', 'Main.java - entry point: look-and-feel, data folder lookup, window start-up'],
    ['**Total**', '**33**', '**6,346**', ''],
  ], [2900, 700, 900, 4166], { align: [AlignmentType.LEFT, AlignmentType.CENTER, AlignmentType.CENTER, AlignmentType.LEFT] }),
  H3('2.6.2.', 'Flowchart of a quiz session'),
  P('Figure 4 shows the flow from start-up to the saved result. After sign-in the user chooses a category and the number of questions; the questions are shuffled and shown one at a time with a countdown. A question ends when the answer is confirmed or the question is skipped (or the timer runs out); the loop continues until no question is left, after which the result is saved and the score screen is shown.'),
  ...figure('fig_flow.png', 'Flowchart of a quiz session', 520),
  H3('2.6.3.', 'Class design'),
  P('Figure 5 shows the most important classes and their relationships. MainWindow owns the screens and creates a QuizSession when a quiz starts. QuizSession owns the list of Question objects and the answer state and can build a QuizResult. StorageService manages accounts, profiles and history, while QuestionBankService loads the question files.'),
  ...figure('fig_class.png', 'Simplified class diagram', 560),
  H3('2.6.4.', 'Key algorithms'),
  P('**Scoring and grading.** With C correct answers out of N questions, the score is S = floor(C x 100 / N) per cent. The grade is A+ for S >= 95, A for S >= 80, B for S >= 65, C for S >= 50 and F below 50. Skipped and unanswered questions earn no marks and are counted separately.'),
  P('**Running average.** After the n-th completed quiz with score s, the stored average becomes avg = floor((avg x (n - 1) + s) / n). Best score is the maximum of all scores.'),
  P('**Day streak.** The date of the last completed quiz is stored. If today equals that date the streak is unchanged; if it equals yesterday the streak is increased by one; otherwise it restarts at one.'),
  P('**50:50 lifeline.** The three wrong option indices are collected and shuffled, and the first two are removed. The correct option is therefore never removed, and the lifeline can be used once per quiz.'),
  P('**Password storage.** On registration a random 16-byte salt is generated; the stored hash is SHA-256(salt + password). On sign-in the same computation is repeated and compared with the stored hash.'),
  P('The pseudo-code of the main quiz loop is given below.', { keepNext: true }),
  ...code([
    'load questions(category) -> shuffle -> take first n',
    'session = new QuizSession(questions, timerSeconds)',
    'for each question q in session:',
    '    show q; secondsRemaining = timerSeconds; start timer',
    '    wait until one of:',
    '        user selects option and confirms  -> session.submitAnswer(option)',
    '        user presses Skip                 -> session.skipCurrent()',
    '        timer reaches 0                   -> submit selection, else skip',
    '    show correct/wrong colours and explanation',
    'result = session.buildResult()      // counts, score, grade, duration',
    'storage.saveResult(result)          // newest first, max 50 kept',
    'profile.recordQuiz(category, score) // totals, best, average, streak',
    'show result screen',
  ]),
  H3('2.6.5.', 'Data design'),
  P('All data are JSON files below the data folder: **questions/** (one file per category), **users/users.json** (accounts), **users/user_<name>.json** (profile and statistics) and **history/history_<name>.json** (past attempts, newest first, at most 50). A small custom JSON parser (about 300 lines) reads and writes these files, so the application has no external library. Sample file contents are given in Appendix-1.'),
];

// ═════════════════════════ CHAPTER 3 ═════════════════════════
const shotFig = (f, cap, w = 500) => figure(S(f), cap, w);

const bugRows = [
  ['B1', 'Rendering', 'Check, cross, refresh and emoji characters (for example in the result pills, the Quit button and Try Again) showed as empty boxes because the UI font lacks them.', 'Replaced by vector-drawn icons in PillBadge and plain text on buttons.'],
  ['B2', 'Rendering', 'Question text such as List<String> lost the part inside the angle brackets because it was placed unescaped into HTML labels.', 'Added HtmlText.escape(); all question, option, hint and explanation text is escaped.'],
  ['B3', 'Rendering', 'Fixed-width HTML text wrapped wider than its card on scaled (125 %) displays because Swing scales CSS pixels by screen DPI.', 'Wrap widths are converted for the screen DPI and re-computed when the window is resized.'],
  ['B4', 'Rendering', 'Long answer options were drawn past the edge of the option card.', 'Options now wrap to two lines and are ellipsised with the full text in a tooltip.'],
  ['B5', 'Rendering', 'Review dialog overflowed horizontally and showed raw code fences (```).', 'Fixed widths, code shown in a monospace block, horizontal scroll removed, Esc closes the dialog.'],
  ['B6', 'Rendering', 'Settings timer drop-down showed white text on a white background.', 'Custom dark combo-box UI and renderer; entries read "30 seconds".'],
  ['B7', 'Rendering', 'The About card text in Settings was clipped.', 'Card height and row spacing corrected.'],
  ['B8', 'Rendering', 'Bright white system scroll bars clashed with the dark theme.', 'Added DarkScrollBarUI and applied it to every scroll pane.'],
  ['B9', 'Layout', 'Back buttons stretched to the height of the two-line page title.', 'Wrapped in a vertically centred panel (UiKit).'],
  ['B10', 'Layout', 'Large uneven gaps on the result screen.', 'Fixed maximum heights and centred the content vertically.'],
  ['B11', 'Layout', 'Quiz content was clipped on short windows; the explanation could be hidden below the fold.', 'Quiz centre area is scrollable and the explanation scrolls into view automatically.'],
  ['B12', 'Layout', 'Lifeline buttons were indented 10 px relative to the question card.', 'Removed the FlowLayout gap and used an explicit spacer.'],
  ['B13', 'Data display', 'Older history entries showed a raw "2026-10-26 18:30" date; the dashboard showed raw ISO dates.', 'Parser accepts several formats; dashboard uses relative dates ("Today", "Yesterday").'],
  ['B14', 'Data display', 'Category cards appeared in arbitrary directory order.', 'Categories are sorted into a fixed, sensible order.'],
  ['B15', 'Behaviour', 'Leaving a running quiz via the sidebar or sign-out silently discarded progress.', 'A confirmation dialog is shown; the timer is paused while it is open and the sidebar highlight is restored on cancel.'],
  ['B16', 'Behaviour', 'Using 50:50 could leave an already-selected, now eliminated option selected, so it could still be submitted.', 'Selection is cleared when the selected option is eliminated.'],
  ['B17', 'Behaviour', 'A question without a hint consumed the one-per-quiz hint lifeline and showed an empty hint.', 'Hint button is disabled for such questions and the lifeline is not consumed.'],
  ['B18', 'Behaviour', 'Quiz key bindings could fire while another screen was visible, and a double Enter could save a result twice.', 'Bindings check that the quiz is showing; completion is guarded by a flag.'],
  ['B19', 'Behaviour', 'The progress bar never reached 100 % and showed a dot at 0 %.', 'Progress counts the answered question; nothing is drawn at 0 %.'],
  ['B20', 'Data', '"Reset All Data" did not clear the history it promised to clear.', 'Added StorageService.clearHistory().'],
  ['B21', 'Data', 'A new account showed a 1-day streak with zero quizzes.', 'New accounts start at 0; the first quiz sets the streak to 1.'],
  ['B22', 'Input', 'Passwords were silently trimmed; user names were not validated although they become file names.', 'Passwords are kept as typed; user names must be 3-20 characters of letters, digits, dot, dash or underscore.'],
  ['B23', 'Usability', 'Settings silently ignored an empty display name; keyboard focus was invisible; no cursor in the user-name box at start-up.', 'Clear warning messages, visible focus ring on Tab navigation, automatic initial focus.'],
];

const e2eRows = [
  ['E1', 'Start a quiz, then check that it is reported as in progress', 'Pass'],
  ['E2', 'Click another sidebar item during a quiz: a confirmation dialog appears', 'Pass'],
  ['E3', 'Answer "No": the quiz stays in progress', 'Pass'],
  ['E4', 'Answer "No": the sidebar highlight returns to "Quizzes"', 'Pass'],
  ['E5', 'Click another item again: the confirmation appears again', 'Pass'],
  ['E6', 'Answer "Yes": the quiz ends and the target screen opens', 'Pass'],
  ['E7', 'Navigate with no quiz running: no dialog is shown', 'Pass'],
  ['E8', 'Save a result, then check it is in the history', 'Pass'],
  ['E9', 'Clear the history: the history becomes empty', 'Pass'],
  ['E10', 'Register a new account', 'Pass'],
  ['E11', 'New account starts with a streak of 0', 'Pass'],
];

const groupRows = [
  ['Grading and scoring', 'T01-T08', '8', '8', '0'],
  ['Quiz session and lifelines', 'T09-T13', '5', '5', '0'],
  ['Profile statistics and streak', 'T14-T18', '5', '5', '0'],
  ['Accounts and security', 'T19-T26', '8', '8', '0'],
  ['Persistence (history, JSON)', 'T27-T31', '5', '5', '0'],
  ['Question bank', 'T32-T36', '5', '5', '0'],
  ['Text safety', 'T37', '1', '1', '0'],
  ['**Total**', '', '**37**', '**37**', '**0**'],
];

const ch3 = [
  ...chapter(3, 'Results Analysis and Validation'),
  H2('3.1.', 'Implementation of Solution'),
  P('The solution was implemented with the tools in Table 6. Every tool is free; the application itself uses only the standard Java platform and no third-party library.'),
  ...table('Tools and technologies used', ['Purpose', 'Tool / technology'], [
    ['Programming language and runtime', 'Java SE (JDK 17 or newer; developed and tested on JDK 26)'],
    ['User interface', 'Java Swing and AWT; custom painting with java.awt.Graphics2D (anti-aliased shapes, arcs, gradients)'],
    ['Audio feedback', 'javax.sound.sampled - tones are generated in code (sine and triangle waves), so no audio files are needed'],
    ['Data storage', 'JSON files read and written by a custom parser (JsonParser); java.nio.file for file access'],
    ['Security', 'java.security.MessageDigest (SHA-256) and SecureRandom for salts'],
    ['Editor and build', 'Visual Studio Code, javac / jar command-line tools and Windows batch scripts (build.bat, run.bat)'],
    ['Version control', 'Git and GitHub (public repository: github.com/SHZunknown11/quizmaster-pro)'],
    ['Testing', 'Plain Java test programs (logic tests, end-to-end navigation test driving the real window, off-screen rendering of every screen for visual inspection)'],
    ['Diagrams and report', 'Python (Pillow) for diagrams, LibreOffice / Word for the report'],
    ['AI assistance', 'Claude Code (an AI coding assistant) was used during development for UI/UX bug review and fixing, test creation and drafting of this report. All results were reviewed by the authors. [[Edit or remove this row to match your university\'s policy on AI assistance.]]'],
  ], [2600, 6066]),
  H2('3.2.', 'Screens of the Implemented Application'),
  P('This section shows each screen exactly as rendered by the application (screenshots at the default window size). The design uses a deep slate background with a warm amber accent, a 72-pixel navigation rail on the left and card-based content.'),
  H3('3.2.1.', 'Login and registration'),
  P('The login screen offers three ways in: sign in with an account, create an account, or continue as a guest. Fields show a focus glow, the password has a show/hide toggle and errors appear in a dismissible banner. A quick-fill link and hint line make the demonstration account easy to use.'),
  ...shotFig('s01_login.png', 'Login screen'),
  ...shotFig('s02_register.png', 'Create-account screen with validation'),
  H3('3.2.2.', 'Dashboard'),
  P('The dashboard greets the user according to the time of day and shows four statistics (total quizzes, best score, day streak and average score), the recent-activity feed and a prominent button to start a new quiz.'),
  ...shotFig('s03b_dashboard_after.png', 'Dashboard with statistics and recent activity'),
  H3('3.2.3.', 'Category selection'),
  P('Four topic cards show the number of available questions and a short description. The user also chooses 5, 10 or 15 questions before starting.'),
  ...shotFig('s04_category.png', 'Category and question-count selection'),
  H3('3.2.4.', 'Quiz screen'),
  P('The quiz screen shows the category and question number, a progress bar, a circular countdown timer, the question, four option cards and the lifelines. Options can be chosen with the mouse or with the keys 1-4 / A-D, and Enter confirms the answer.'),
  ...shotFig('s05_quiz.png', 'Quiz screen with timer and four options'),
  ...shotFig('s06_quiz_lifelines.png', 'After using the 50:50 lifeline and the hint'),
  ...shotFig('s07_quiz_answered.png', 'Answer feedback: correct option in green, wrong choice in red, with explanation'),
  H3('3.2.5.', 'Result and review'),
  P('At the end the score is shown on an animated ring together with the numbers of correct, wrong and skipped questions and the grade. The user can try the same quiz again, start a new one, or open the question-by-question review.'),
  ...shotFig('s08_result.png', 'Result screen with score ring, breakdown and grade'),
  ...shotFig('s11_review.png', 'Question-by-question review dialog', 440),
  H3('3.2.6.', 'History and settings'),
  ...shotFig('s09_history.png', 'Quiz history with score, grade and duration'),
  ...shotFig('s10_settings.png', 'Settings: display name, sound, timer length, data reset'),
  H2('3.3.', 'Testing, Characterization and Data Validation'),
  P('Testing combined four complementary methods. First, **automated logic tests** (37 checks) exercise the model and service classes without a window. Second, an **end-to-end test** drives the real main window and the confirmation dialogs. Third, every screen was **rendered off-screen** at the minimum window size and inspected. Fourth, a **clean-clone test** checked that a fresh copy of the repository builds and starts on a machine that has only the JDK.'),
  H3('3.3.1.', 'Automated logic tests'),
  P('Table 7 summarises the 37 logic checks; the complete list with descriptions is in Appendix-2. All of them passed. The tests use a temporary data folder, so they never touch real user data.'),
  ...table('Summary of automated logic tests', ['Group', 'Test IDs', 'Tests', 'Passed', 'Failed'], groupRows,
    [3200, 1300, 1300, 1433, 1433], { keep: true, align: [AlignmentType.LEFT, AlignmentType.CENTER, AlignmentType.CENTER, AlignmentType.CENTER, AlignmentType.CENTER] }),
  P('Several results are worth highlighting: the 50:50 lifeline was run 2,000 times with random correct positions and never removed the correct option (T09); grade boundaries behave exactly at 95, 80, 65, 50 and 49 per cent (T01-T05); a quiz with no questions yields 0 per cent without a division error (T07); the stored hash is a 64-character SHA-256 value that does not contain the password, and the same password gets a different salt and hash in each account (T20, T21); and a JSON round trip preserves quotes, new lines, back-slashes and Unicode (T31).'),
  H3('3.3.2.', 'End-to-end navigation test'),
  P('The real MainWindow was started, a quiz was begun and the sidebar was used to leave it. A helper thread answered the confirmation dialogs automatically. Table 8 shows the checks; all passed.'),
  ...table('End-to-end checks on the real application window', ['ID', 'Check', 'Result'], e2eRows, [700, 6866, 1100],
    { keep: true, align: [AlignmentType.CENTER, AlignmentType.LEFT, AlignmentType.CENTER] }),
  H3('3.3.3.', 'Visual and clean-clone validation'),
  P('All eight screens and the review dialog were rendered at a content size of 848 x 600 pixels, which is smaller than the default window, and checked for clipped text, overlapping elements, unreadable colours and missing glyphs. The defects found this way are listed in Section 3.4. A fresh copy of the GitHub repository was cloned into an empty folder, compiled with javac, packaged with jar and started; the application launched and created its demonstration account automatically.'),
  H2('3.4.', 'Defects Found and Corrected'),
  P('The review and testing described above uncovered 23 defects, which were corrected. Table 9 lists them by type.'),
  ...table('Defects found during testing and their corrections', ['ID', 'Type', 'Defect', 'Correction'], bugRows, [560, 1300, 3700, 3106],
    { align: [AlignmentType.CENTER, AlignmentType.LEFT, AlignmentType.LEFT, AlignmentType.LEFT] }),
  H2('3.5.', 'Interpretation of Results'),
  P('All 37 logic checks and all 11 end-to-end checks passed after the corrections, and every screen renders without clipping at the minimum window size. The most valuable finding of the validation was that several defects were **invisible in the source code but obvious on screen** - for example the missing glyphs, the white drop-down and the DPI-dependent text wrapping - which shows the importance of rendering and inspecting the interface and not only testing the logic. Equally, defects such as the quiz being abandoned without warning or the reset button not clearing history were logic or behaviour problems that only appeared when the application was used the way a student would use it.'),
  P('The size of the delivered software is 33 classes and 6,346 lines of Java, together with a question bank of 60 questions (19 easy, 26 medium and 15 hard) spread over four categories. A student needs only a JDK to build and run it.'),
];

// ═════════════════════════ CHAPTER 4 ═════════════════════════
const ch4 = [
  ...chapter(4, 'Conclusion and Future Work'),
  H2('4.1.', 'Conclusion'),
  P('The project set out to give Java students an offline, distraction-free quiz tool with instant feedback and visible progress. The finished application meets this objective: it provides secure sign-in, four topic categories with 60 questions, a per-question timer, 50:50 and hint lifelines, explanations, a graded result with review, a persistent history with statistics, and configurable settings, all in a consistent, modern interface that runs from a single JAR file. The layered architecture separates the screens, the model and the storage, which made it possible to test the logic without a window and to fix interface defects without touching the data code.'),
  P('**Expected results.** The expected outcome was a working quiz application that a student can build and use without installing anything but the JDK. This was achieved and verified by the clean-clone test.'),
  P('**Deviations from the expected results and their reasons.**'),
  bullet('The application stores data in JSON files instead of a database. This was a deliberate decision (Section 2.5) to remove set-up effort; the consequence is that complex queries and very large data sets are not supported.'),
  bullet('Online multiplayer and leaderboards were not implemented because they would require a server and the storage of other users\' data (Section 2.3).'),
  bullet('The look of the interface depends on the Segoe UI font that ships with Windows; on other operating systems a substitute font is used and spacing can differ slightly.'),
  bullet('The question bank is limited to 60 questions; a larger bank would give more variety between attempts.'),
  bullet('Passwords are hashed with salted SHA-256, which is adequate for a local study tool but is not a deliberately slow password-hashing function such as bcrypt or Argon2 as would be needed for an internet-facing service.'),
  H2('4.2.', 'Future Work'),
  P('The following modifications and extensions are suggested as the way ahead:'),
  bullet('**Question editor and import/export** - add or edit questions inside the application and import question sets from CSV or JSON files.'),
  bullet('**Larger and adaptive question bank** - more topics (exception handling, multithreading, streams, JDBC) and difficulty that adapts to the user\'s performance.'),
  bullet('**Progress analytics** - charts of score over time and per-topic strengths and weaknesses.'),
  bullet('**Database back end** - replace the file storage by SQLite behind the existing StorageService interface to support larger data and multiple devices.'),
  bullet('**Automated unit-test suite** - convert the current test programs into a JUnit suite run by a build tool such as Maven or Gradle, and add continuous integration on GitHub.'),
  bullet('**Accessibility and portability** - a high-contrast and light theme, scalable fonts, full keyboard navigation and a native installer created with jpackage.'),
  bullet('**Stronger password hashing** - use PBKDF2 or Argon2 for stored passwords.'),
];

// ═════════════════════════ REFERENCES / APPENDICES / MANUAL / ACHIEVEMENTS ═════════════════════════
const refs = [
  'Bloch, J. (2018) *Effective Java*. 3rd edn. Boston: Addison-Wesley.',
  'Chacon, S. and Straub, B. (2014) *Pro Git*. 2nd edn. New York: Apress.',
  'ECMA International (2017) *ECMA-404: The JSON Data Interchange Syntax*. 2nd edn. Geneva: Ecma International.',
  'Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994) *Design Patterns: Elements of Reusable Object-Oriented Software*. Reading, MA: Addison-Wesley.',
  'Grassi, P. A., Garcia, M. E. and Fenton, J. L. (2017) *Digital Identity Guidelines: Authentication and Lifecycle Management*. NIST Special Publication 800-63B. Gaithersburg, MD: National Institute of Standards and Technology.',
  'Krasner, G. E. and Pope, S. T. (1988) \'A cookbook for using the model-view-controller user interface paradigm in Smalltalk-80\', *Journal of Object-Oriented Programming*, 1(3), pp. 26-49.',
  'Nielsen, J. (1994) *10 Usability Heuristics for User Interface Design*. Nielsen Norman Group.',
  'Oracle (n.d.) *The Java Tutorials: Creating a GUI With Swing*. Oracle Corporation. Available at: https://docs.oracle.com/javase/tutorial/uiswing/',
  'Oracle (n.d.) *Java Platform, Standard Edition API Specification* (java.awt.Graphics2D, javax.swing, javax.sound.sampled, java.security.MessageDigest). Oracle Corporation. Available at: https://docs.oracle.com/en/java/javase/',
  'W3C (2018) *Web Content Accessibility Guidelines (WCAG) 2.1*. World Wide Web Consortium.',
];
const refsBlock = [
  plainChapter('References', 'ref'),
  ...refs.map((r, i) => new Paragraph({
    alignment: AlignmentType.JUSTIFIED, spacing: { line: 360, after: 120 }, indent: { left: 600, hanging: 600 },
    tabStops: [{ type: D.TabStopType.LEFT, position: 600 }],
    children: [new TextRun({ text: `${i + 1}.\t`, font: FONT, size: 24 }), ...runs(r, { size: 24 })],
  })),
];

const logicLines = fs.readFileSync(path.join(HERE, 'logic_results.txt'), 'utf8').split(/\r?\n/).filter((l) => /^(PASS|FAIL)/.test(l));
const logicRows = logicLines.map((l) => {
  const [res, id, ...rest] = l.split(' | ');
  return [id, rest.join(' | '), res === 'PASS' ? 'Pass' : 'FAIL'];
});

const appendix = [
  plainChapter('Appendix', 'app'),
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 200 },
    children: [new TextRun({ text: `Appendices of the project report "${TITLE_CASE}"`, italics: true, size: 24, font: FONT })] }),
  H2('A1.', 'Appendix-1: Project Structure and Data Formats'),
  P('The repository layout is shown below.'),
  ...code([
    'quizmaster-pro/',
    '  src/com/quizmaster/',
    '    Main.java            entry point',
    '    view/        (10)    MainWindow, SidebarNav, LoginPanel,',
    '                         DashboardPanel, CategorySelectPanel,',
    '                         QuizPanel, ResultPanel, HistoryPanel,',
    '                         SettingsPanel, ReviewAnswersDialog',
    '    components/  (8)     RoundedCard, PrimaryButton, SecondaryButton,',
    '                         OptionCardButton, StatCard, PillBadge,',
    '                         CircularScoreMeter, AuthInputField',
    '    model/       (6)     Question, QuizSession, QuizResult,',
    '                         UserAccount, UserProfile, Category',
    '    service/     (4)     StorageService, QuestionBankService,',
    '                         JsonParser, AudioService',
    '    util/        (4)     AppTheme, HtmlText, DarkScrollBarUI, UiKit',
    '  data/',
    '    questions/   java_fundamentals.json, oop_concepts.json,',
    '                 collections_framework.json, data_structures.json',
    '    users/       users.json, user_<name>.json    (created at run time)',
    '    history/     history_<name>.json             (created at run time)',
    '  build.bat   run.bat   README.md',
  ]),
  P('A question in a question-bank file:'),
  ...code([
    '{',
    '  "id": "jf01",',
    '  "difficulty": "EASY",',
    '  "questionText": "Which keyword is used to define a class in Java?",',
    '  "options": ["struct", "class", "define", "object"],',
    '  "correctOptionIndex": 1,',
    '  "hint": "It\'s the same keyword used in C++ and C#.",',
    '  "explanation": "The \'class\' keyword is used to declare a class in Java."',
    '}',
  ]),
  P('An account record in users.json (the password itself is never stored):'),
  ...code([
    '{',
    '  "username": "taha",',
    '  "displayName": "Taha",',
    '  "salt": "0404db00d03ccd18c2545ff6e42a8fc9",',
    '  "passwordHash": "c03066ed3e540ec24ca2570a979bc107...cc128fb33",',
    '  "createdAt": "2026-10-27T14:59:57.981772900"',
    '}',
  ]),
  P('A history record in history_<name>.json:'),
  ...code([
    '{',
    '  "id": "23393446-eb29-4856-8a02-01792081867a",',
    '  "category": "OOP Concepts",',
    '  "totalQuestions": 10, "correct": 1, "wrong": 1, "skipped": 8,',
    '  "scorePercent": 10, "durationSeconds": 52, "grade": "F",',
    '  "timestamp": "2026-10-28T13:52:27.772375400"',
    '}',
  ]),
  H2('A2.', 'Appendix-2: Selected Source Code'),
  P('The 50:50 lifeline from QuizSession.java. The correct option is never added to the list of candidates for removal:'),
  ...code([
    'public int[] useFiftyFifty() {',
    '    if (fiftyFiftyUsed) return null;',
    '    fiftyFiftyUsed = true;',
    '',
    '    Question q = getCurrentQuestion();',
    '    List<Integer> wrongIndices = new ArrayList<>();',
    '    for (int i = 0; i < 4; i++) {',
    '        if (i != q.getCorrectIndex()) wrongIndices.add(i);',
    '    }',
    '    Collections.shuffle(wrongIndices);   // pick 2 random wrong ones',
    '    return new int[]{ wrongIndices.get(0), wrongIndices.get(1) };',
    '}',
  ]),
  P('Escaping text before it is placed in a Swing HTML label (HtmlText.java), which fixes the loss of generic types such as List<String>:'),
  ...code([
    'public static String escape(String s) {',
    '    if (s == null) return "";',
    '    return s.replace("&", "&amp;")',
    '            .replace("<", "&lt;")',
    '            .replace(">", "&gt;")',
    '            .replace("\\r\\n", "\\n")',
    '            .replace("\\n", "<br>");',
    '}',
  ]),
  P('Grade calculation from the percentage score (QuizResult.java):'),
  ...code([
    'if (scorePercent >= 95) grade = "A+";',
    'else if (scorePercent >= 80) grade = "A";',
    'else if (scorePercent >= 65) grade = "B";',
    'else if (scorePercent >= 50) grade = "C";',
    'else grade = "F";',
  ]),
  H2('A3.', 'Appendix-3: Complete List of Automated Logic Tests'),
  P('The tests are run by a plain Java program against the compiled classes. Output of the final run: 37 tests, 37 passed, 0 failed.', { keepNext: true }),
  ...table('Automated logic tests and results', ['ID', 'Test description', 'Result'], logicRows, [700, 6866, 1100],
    { align: [AlignmentType.CENTER, AlignmentType.LEFT, AlignmentType.CENTER] }),
  H2('A4.', 'Appendix-4: Source Code Repository'),
  P('The complete source code, question banks, build scripts and README are available in the public repository: **https://github.com/SHZunknown11/quizmaster-pro**.'),
];

// user manual ----------------------------------------------------------------
const manual = [
  plainChapter('User Manual', 'manual'),
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 200 },
    children: [new TextRun({ text: '(Complete step-by-step instructions to run and use the project)', italics: true, size: 24, font: FONT })] }),
  H2('M1.', 'Requirements'),
  bullet('A computer with **Windows** (recommended; Linux and macOS also work, see M3).'),
  bullet('A **JDK 17 or newer** (a JRE alone is not enough because the project is compiled on your machine). Check with the command `javac -version`.'),
  bullet('**Git** to download the project (or download the ZIP file from GitHub).'),
  H2('M2.', 'Download, Build and Run (Windows)'),
  ...numbered([
    'Open a terminal (Command Prompt or PowerShell).',
    'Download the project: `git clone https://github.com/SHZunknown11/quizmaster-pro.git`',
    'Go into the folder: `cd quizmaster-pro`',
    'Build it by double-clicking **build.bat** (or run it in the terminal). It compiles the code into the bin folder and creates **QuizMasterPro.jar**. Press a key when it says "Build complete".',
    'Start the program by double-clicking **run.bat**, or run `java -jar QuizMasterPro.jar`.',
    'Always start the program from the project folder, because it looks for the data folder next to it.',
  ]),
  H2('M3.', 'Run on Linux or macOS'),
  ...code([
    'git clone https://github.com/SHZunknown11/quizmaster-pro.git',
    'cd quizmaster-pro',
    'mkdir -p bin',
    'javac -encoding UTF-8 -d bin -sourcepath src src/com/quizmaster/Main.java',
    'java -cp bin com.quizmaster.Main',
  ]),
  H2('M4.', 'Signing In'),
  ...numbered([
    'On the login screen enter a user name and password and press **Sign In** (or Enter). For a quick demonstration use the account **taha / taha123**; the "Quick-fill" link fills it in for you.',
    'To create your own account choose the **Create Account** tab, enter a full name, a user name (3-20 letters, digits, dot, dash or underscore) and a password of at least four characters, confirm it and press **Create Account**.',
    'To try the program without an account press **Continue as Guest**.',
    'The eye icon in a password field shows or hides the typed password.',
  ]),
  ...figure(S('s01_login.png'), 'User manual: login screen', 380),
  H2('M5.', 'Dashboard and Navigation'),
  P('After signing in the dashboard shows your statistics and recent activity. Use the icons in the left rail to move between screens: Home (dashboard), Quizzes (start a quiz), History, Settings (bottom) and Log Out (bottom). Hover over an icon to see its name. Press **Start New Quiz** to begin.'),
  ...figure(S('s03b_dashboard_after.png'), 'User manual: dashboard', 380),
  H2('M6.', 'Taking a Quiz'),
  ...numbered([
    'Choose a topic by clicking one of the four category cards; the selected card is outlined in amber.',
    'Choose the number of questions (5, 10 or 15) and press **Start Quiz**.',
    'Read the question. The ring at the top right counts down the time left for the question.',
    'Select an answer by clicking an option or pressing **1-4** or **A-D**, then press **Confirm Answer** or **Enter**.',
    'The correct option turns green; a wrong choice turns red; an explanation appears below. Press **Next Question** or **Enter** to continue.',
    '**Skip Question** moves on without answering. If the timer reaches zero, the selected option is submitted, or the question is skipped if nothing was selected.',
    'Use **50:50** to remove two wrong options and **View Hint** to see a hint. Each can be used only once per quiz.',
    'Press **Quit** (or any other navigation item) to leave a quiz; the program asks for confirmation because progress is lost.',
  ]),
  ...figure(S('s04_category.png'), 'User manual: choosing a category and number of questions', 380),
  ...figure(S('s05_quiz.png'), 'User manual: answering a question', 380),
  H2('M7.', 'Keyboard Shortcuts'),
  ...table('Keyboard shortcuts during a quiz', ['Key', 'Action'], [
    ['1, 2, 3, 4 or A, B, C, D', 'Select option 1-4 (A-D)'],
    ['Enter', 'Confirm the selected answer; after confirming, go to the next question or to the results'],
    ['Tab / Shift+Tab', 'Move the keyboard focus between buttons (a blue ring shows the focused button)'],
    ['Esc', 'Close the answer-review dialog'],
  ], [3000, 5666], { keep: true }),
  H2('M8.', 'Results and Review'),
  P('When the last question is finished the result screen shows your score on a ring, the numbers of correct, wrong and skipped questions and your grade (A+ from 95 %, A from 80 %, B from 65 %, C from 50 %, otherwise F). Press **Try Again** to repeat the same category, **New Quiz** to choose another, or **Review Answers** to see every question with your answer, the correct answer and the explanation.'),
  ...figure(S('s08_result.png'), 'User manual: result screen', 380),
  H2('M9.', 'History and Settings'),
  P('The **History** screen lists your most recent attempts (up to 50) with date, duration, score, correct count and grade. The **Settings** screen lets you change your display name, switch sound on or off, set the question timer to 15, 30, 45 or 60 seconds, sign out, or reset all statistics and history (a confirmation is required and the reset cannot be undone).'),
  ...figure(S('s10_settings.png'), 'User manual: settings screen', 380),
  H2('M10.', 'Troubleshooting'),
  ...table('Common problems and solutions', ['Problem', 'Solution'], [
    ['"javac" is not recognised', 'Install a JDK (not only a JRE) and make sure its bin folder is in the PATH, then open a new terminal.'],
    ['The window does not start or no questions are shown', 'Start the program from the project folder so that the data folder is found; do not move or rename data/questions.'],
    ['Text looks different or fonts are missing', 'The design uses the Segoe UI font of Windows; other systems use a substitute font.'],
    ['No sound', 'Open Settings and tick "Sound Enabled"; check the system volume.'],
    ['Forgot the password', 'Create a new account, or delete the files in data/users to reset all accounts (the demonstration account is re-created automatically).'],
  ], [2900, 5766]),
];

const achievements = [
  plainChapter('Achievements', 'ach'),
  P('[[Add your achievements here, for example: presentation of the project in class, certificates, hackathon or competition participation, publication, or recognition by the faculty. If there are none, write "Not applicable".]]'),
  P('Project outcomes:'),
  bullet('A complete Java Swing application of 33 classes and about 6,300 lines of code with no third-party dependencies.'),
  bullet('A 60-question Java question bank in four categories.'),
  bullet('37 automated logic checks and 11 end-to-end checks, all passing.'),
  bullet('A public GitHub repository with build scripts and documentation: github.com/SHZunknown11/quizmaster-pro.'),
];

// ═════════════════════════ FRONT MATTER ═════════════════════════
const centerLine = (t, o = {}) => new Paragraph({
  alignment: AlignmentType.CENTER, spacing: { before: o.before ?? 0, after: o.after ?? 0, line: o.line || 276 },
  children: [new TextRun({ text: t, font: FONT, size: o.size || 28, bold: o.bold, italics: o.italics })],
});
const logoImg = (w) => {
  const f = path.join(HERE, 'logo_p10_51.png');
  const b = fs.readFileSync(f);
  const iw = b.readUInt32BE(16), ih = b.readUInt32BE(20);
  return new Paragraph({
    alignment: AlignmentType.CENTER, spacing: { before: 120, after: 120 },
    children: [new ImageRun({ type: 'png', data: b, transformation: { width: w, height: Math.round(w * ih / iw) },
      altText: { title: 'Chandigarh University logo', description: 'Chandigarh University logo', name: 'cu-logo' } })],
  });
};

const titlePage = [
  centerLine(TITLE, { bold: true, size: 32, before: 480, after: 360, line: 320 }),
  centerLine('A PROJECT REPORT', { bold: true, size: 28, after: 120 }),
  centerLine(`Subject: ${SUBJECT}`, { size: 24, after: 480 }),
  centerLine('Submitted by', { bold: true, italics: true, size: 28, after: 240 }),
  ...NAMES.map(([n, u]) => centerLine(`${n} (${u})`, { size: 28, after: 80 })),
  centerLine('in partial fulfillment for the award of the degree of', { bold: true, italics: true, size: 28, before: 360, after: 360 }),
  centerLine('BACHELOR OF ENGINEERING', { bold: true, size: 28, after: 200 }),
  centerLine('IN', { bold: true, size: 28, after: 200 }),
  centerLine('COMPUTER SCIENCE AND ENGINEERING', { size: 28, after: 240 }),
  logoImg(230),
  centerLine('Chandigarh University', { bold: true, size: 28, after: 360 }),
  centerLine(MONTH_YEAR, { size: 28 }),
];

const sigCell = (lines, w, o = {}) => new TableCell({
  width: { size: w, type: WidthType.DXA }, borders: noBorders, margins: { top: 40, bottom: 40, left: 0, right: 120 },
  children: lines.map((t) => new Paragraph({ spacing: { line: 276, after: 40 }, children: runs(t, { size: 24, bold: o.bold }) })),
});
const bonafide = [
  new Paragraph({ pageBreakBefore: true, children: [new TextRun({ text: '', font: FONT })] }),
  logoImg(210),
  centerLine('BONAFIDE CERTIFICATE', { bold: true, size: 32, before: 120, after: 240 }),
  new Paragraph({
    alignment: AlignmentType.JUSTIFIED, spacing: { line: 480, after: 240 },
    children: [
      new TextRun({ text: 'Certified that this project report ', font: FONT, size: 28 }),
      new TextRun({ text: `“${TITLE}”`, font: FONT, size: 28, bold: true }),
      new TextRun({ text: ' is the bonafide work of ', font: FONT, size: 28 }),
      new TextRun({ text: `“${NAMES.map((n) => n[0]).join(' and ')}”`, font: FONT, size: 28, bold: true }),
      new TextRun({ text: ' who carried out the project work under my/our supervision.', font: FONT, size: 28 }),
    ],
  }),
  new Table({
    width: { size: TEXTW, type: WidthType.DXA }, columnWidths: [4333, 4333],
    rows: [
      new TableRow({ children: [sigCell(['', '', 'SIGNATURE'], 4333, { bold: true }), sigCell(['', '', 'SIGNATURE'], 4333, { bold: true })] }),
      new TableRow({ children: [
        sigCell(['[[Name of the Head of the Department]]', '**HEAD OF THE DEPARTMENT**', '[[Department of Computer Science and Engineering]]'], 4333),
        sigCell([SUPERVISOR, '**SUPERVISOR**', '[[Academic designation, e.g. Assistant Professor]]', '[[Department of Computer Science and Engineering]]', ADDRESS], 4333),
      ] }),
    ],
  }),
  new Paragraph({ spacing: { before: 360, after: 360, line: 276 },
    children: [new TextRun({ text: 'Submitted for the project viva-voce examination held on ', font: FONT, size: 24 }), new TextRun({ text: '__ / __ / ____', font: FONT, size: 24, highlight: 'yellow' })] }),
  new Table({
    width: { size: TEXTW, type: WidthType.DXA }, columnWidths: [4333, 4333],
    rows: [new TableRow({ children: [sigCell(['**INTERNAL EXAMINER**'], 4333), sigCell(['**EXTERNAL EXAMINER**'], 4333)] })],
  }),
];

const ack = [
  frontHeading('Acknowledgement', 'front:ack', false),
  P('We take this opportunity to express our sincere gratitude to everyone who supported us in completing this project.'),
  P(`We are deeply thankful to our faculty guide and supervisor, **${SUPERVISOR}**, for his valuable guidance, constructive feedback and constant encouragement throughout the Project Based Learning in Java course. His suggestions helped us to improve the design, the testing and the quality of this report.`),
  P('We thank the **Head of the Department** and the faculty of the Department of Computer Science and Engineering, **Chandigarh University**, for providing the laboratory facilities and a learning environment in which we could carry out this work.'),
  P('We are also grateful to our classmates for testing the application and giving honest feedback on its usability, and to our families for their patience and support.'),
  P('Finally, we acknowledge the open documentation and tutorials of the Java platform, which made it possible to build the whole application with the standard library alone.'),
  ...NAMES.map(([n, u], i) => new Paragraph({ alignment: AlignmentType.RIGHT, spacing: { before: i === 0 ? 360 : 0 }, children: runs(`**${n}** (${u})`, { size: 24 }) })),
];

// abstract: one page, double spacing, 14 pt (format guide 2.3)
const abstractText = 'QuizMaster Pro is a desktop quiz application developed in Java with the Swing toolkit and Java 2D graphics to help students practise core Java topics. The project addresses the lack of a free, offline, distraction-free tool that gives instant feedback, tracks progress over time and works without an internet connection or a database server. The application provides secure sign-in with salted SHA-256 password hashing, four topic banks containing sixty multiple-choice questions, a per-question countdown timer, 50:50 and hint lifelines, instant explanations, a score screen with grade and answer review, a history of previous attempts and day-streak statistics. A layered, MVC-style architecture separates the view, model and service packages, and all data are stored in human-readable JSON files handled by a custom parser, so no external library is needed. Three alternative designs were compared and the Swing-plus-JSON design was selected. Testing combined thirty-seven automated logic checks, end-to-end navigation tests and visual inspection of every screen; all checks passed and twenty-three interface and behaviour defects found during review were corrected. The result is a portable, zero-dependency application of 33 classes and about 6,300 lines of code that runs on any computer with JDK 17 or later.';
const abstractPage = [
  frontHeading('Abstract', 'front:abs'),
  new Paragraph({ alignment: AlignmentType.JUSTIFIED, spacing: { line: 480, after: 0 }, children: [new TextRun({ text: abstractText, font: FONT, size: 28 })] }),
];

const graphicalPage = [
  frontHeading('Graphical Abstract', 'front:gabs'),
  ...graphicalFig,
  P('The student signs in, chooses a topic, answers timed questions with instant feedback and sees a graded result. The Java application core applies the question bank, timer, lifelines and scoring, and every account, statistic and attempt is stored in local JSON files.', { size: 24 }),
];

const abbrevRows = [
  ['API', 'Application Programming Interface'], ['AWT', 'Abstract Window Toolkit'], ['CSE', 'Computer Science and Engineering'],
  ['CSS', 'Cascading Style Sheets'], ['DPI', 'Dots Per Inch'], ['EDT', 'Event Dispatch Thread (Swing)'],
  ['FXML', 'JavaFX Markup Language'], ['GUI', 'Graphical User Interface'], ['HTML', 'HyperText Markup Language'],
  ['IDE', 'Integrated Development Environment'], ['JAR', 'Java Archive'], ['JDK', 'Java Development Kit'],
  ['JSON', 'JavaScript Object Notation'], ['MCQ', 'Multiple-Choice Question'], ['MVC', 'Model-View-Controller'],
  ['OOP', 'Object-Oriented Programming'], ['SHA-256', 'Secure Hash Algorithm, 256-bit'], ['UI / UX', 'User Interface / User Experience'],
  ['UID', 'Unique Identification Number'], ['WCAG', 'Web Content Accessibility Guidelines'],
];
const abbrevPage = [
  frontHeading('Abbreviations', 'front:abb'),
  ...table(null, ['Abbreviation', 'Full form'], abbrevRows, [2200, 6466]),
];
const symbolRows = [
  ['C', 'Number of correctly answered questions'], ['N', 'Total number of questions in a quiz'],
  ['S', 'Score in per cent, S = floor(C x 100 / N)'], ['s', 'Seconds (unit of time)'],
  ['T', 'Timer length per question in seconds (15, 30, 45 or 60)'], ['n', 'Number of quizzes completed by a user'],
  ['avg', 'Rolling average score in per cent'], ['%', 'Per cent'],
];
const symbolPage = [
  frontHeading('Symbols', 'front:sym'),
  ...table(null, ['Symbol', 'Meaning'], symbolRows, [2200, 6466]),
];

// ═════════════════════════ TABLE OF CONTENTS / LISTS (built after registries are filled) ═════════════════════════
const body = [...ch1, ...ch2, ...ch3, ...ch4, ...refsBlock, ...appendix, ...manual, ...achievements];

const frontKeys = [['List of Figures', 'front:lof'], ['List of Tables', 'front:lot'], ['Abstract', 'front:abs'],
  ['Graphical Abstract', 'front:gabs'], ['Abbreviations', 'front:abb'], ['Symbols', 'front:sym']];
const tocLines = [];
tocLines.push(tocLine('', 'Acknowledgement', pg('front:ack')));
frontKeys.forEach(([t, k]) => tocLines.push(tocLine('', t, pg(k))));
reg.toc.filter((e) => !e.front).forEach((e) => {
  if (e.level === 0) tocLines.push(tocLine(e.label, e.title, pg(e.key), 0, { labelW: e.label ? 1500 : 0 }));
  else if (e.level === 1) tocLines.push(tocLine(e.label, e.title, pg(e.key), 300, { labelW: 700 }));
  else tocLines.push(tocLine(e.label, e.title, pg(e.key), 600, { labelW: 800 }));
});
const tocPage = [
  frontHeading('Table of Contents', 'front:toc', false),
  ...tocLines,
];

const lofPage = [
  frontHeading('List of Figures', 'front:lof', false),
  ...reg.figs.map((f) => tocLine(`Figure ${f.no}:`, f.caption, pg(f.key), 0, { labelW: 1300 })),
];
const lotPage = [
  frontHeading('List of Tables', 'front:lot', false),
  ...reg.tabs.map((t) => tocLine(`Table ${t.no}:`, t.caption, pg(t.key), 0, { labelW: 1200 })),
];

// ═════════════════════════ DOCUMENT ═════════════════════════
const pageProps = (extra = {}) => ({
  page: { size: { width: 11906, height: 16838 }, margin: { top: 1440, bottom: 1300, left: 1800, right: 1440, header: 600, footer: 600 }, ...extra },
});
const footer = () => new Footer({ children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ children: [PageNumber.CURRENT], font: FONT, size: 22 })] })] });

const doc = new Document({
  creator: 'Mohammad Taha, Sarwar Adhikari', title: TITLE_CASE, description: 'Lab Based Mini Project Report - Chandigarh University',
  styles: {
    default: { document: { run: { font: FONT, size: 24 } } },
    paragraphStyles: [
      { id: 'Heading1', name: 'Heading 1', basedOn: 'Normal', next: 'Normal', quickFormat: true, run: { size: 32, bold: true, font: FONT, color: '000000' }, paragraph: { spacing: { before: 240, after: 240 }, outlineLevel: 0 } },
      { id: 'Heading2', name: 'Heading 2', basedOn: 'Normal', next: 'Normal', quickFormat: true, run: { size: 28, bold: true, font: FONT, color: '000000' }, paragraph: { spacing: { before: 200, after: 120 }, outlineLevel: 1 } },
      { id: 'Heading3', name: 'Heading 3', basedOn: 'Normal', next: 'Normal', quickFormat: true, run: { size: 24, bold: true, font: FONT, color: '000000' }, paragraph: { spacing: { before: 160, after: 100 }, outlineLevel: 2 } },
    ],
  },
  numbering: {
    config: [
      { reference: 'bul', levels: [{ level: 0, format: LevelFormat.BULLET, text: '•', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 720, hanging: 360 } } } }] },
      { reference: 'num', levels: [{ level: 0, format: LevelFormat.DECIMAL, text: '%1.', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 720, hanging: 360 } } } }] },
    ],
  },
  sections: [
    { properties: pageProps(), children: [...titlePage, ...bonafide] },
    { properties: { ...pageProps({ pageNumbers: { start: 1, formatType: NumberFormat.LOWER_ROMAN } }), type: SectionType.NEXT_PAGE },
      footers: { default: footer() },
      children: [...ack, ...tocPage, ...lofPage, ...lotPage, ...abstractPage, ...graphicalPage, ...abbrevPage, ...symbolPage] },
    { properties: { ...pageProps({ pageNumbers: { start: 1, formatType: NumberFormat.DECIMAL } }), type: SectionType.NEXT_PAGE },
      footers: { default: footer() }, children: body },
  ],
});

Packer.toBuffer(doc).then((buf) => {
  fs.writeFileSync(path.join(HERE, 'Project_Report.docx'), buf);
  fs.writeFileSync(path.join(HERE, 'registry.json'), JSON.stringify({ toc: reg.toc, figs: reg.figs, tabs: reg.tabs }, null, 1));
  console.log('docx written; figures', reg.figs.length, 'tables', reg.tabs.length, 'toc entries', reg.toc.length);
});
