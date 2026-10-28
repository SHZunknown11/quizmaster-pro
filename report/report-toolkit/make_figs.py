from PIL import Image, ImageDraw, ImageFont
import os

OUT = os.path.dirname(os.path.abspath(__file__))
FD = r"C:\Windows\Fonts"
S = 2  # supersample factor


def font(size, bold=False):
    return ImageFont.truetype(os.path.join(FD, "arialbd.ttf" if bold else "arial.ttf"), size * S)


def mono(size):
    return ImageFont.truetype(os.path.join(FD, "consola.ttf"), size * S)


BLACK = (0, 0, 0)
GREY1 = (240, 240, 240)
GREY2 = (215, 215, 215)
GREY3 = (170, 170, 170)


class Canvas:
    def __init__(self, w, h):
        self.im = Image.new("RGB", (w * S, h * S), "white")
        self.d = ImageDraw.Draw(self.im)

    def rect(self, x, y, w, h, fill=GREY1, outline=BLACK, width=2, r=8):
        self.d.rounded_rectangle([x * S, y * S, (x + w) * S, (y + h) * S], radius=r * S,
                                 fill=fill, outline=outline, width=width * S)

    def text(self, x, y, s, f, anchor="la", fill=BLACK):
        self.d.text((x * S, y * S), s, font=f, fill=fill, anchor=anchor)

    def center_text(self, x, y, w, h, lines, f, lh=None, fill=BLACK):
        if isinstance(lines, str):
            lines = [lines]
        lh = lh or (f.size / S) * 1.25
        total = lh * len(lines)
        cy = y + (h - total) / 2
        for i, ln in enumerate(lines):
            self.text(x + w / 2, cy + i * lh + lh / 2, ln, f, anchor="mm", fill=fill)

    def box(self, x, y, w, h, lines, f=None, fill=GREY1, bold=False, r=8):
        f = f or font(15, bold)
        self.rect(x, y, w, h, fill=fill, r=r)
        self.center_text(x, y, w, h, lines, f)

    def line(self, pts, width=2, arrow=True, dash=False):
        pts2 = [(px * S, py * S) for px, py in pts]
        self.d.line(pts2, fill=BLACK, width=width * S, joint="curve")
        if arrow:
            self.arrow_head(pts[-2], pts[-1])

    def arrow_head(self, p0, p1, size=10):
        import math
        ang = math.atan2(p1[1] - p0[1], p1[0] - p0[0])
        a1 = ang + math.radians(150)
        a2 = ang - math.radians(150)
        pts = [(p1[0] * S, p1[1] * S),
               ((p1[0] + size * math.cos(a1)) * S, (p1[1] + size * math.sin(a1)) * S),
               ((p1[0] + size * math.cos(a2)) * S, (p1[1] + size * math.sin(a2)) * S)]
        self.d.polygon(pts, fill=BLACK)

    def diamond(self, x, y, w, h, lines, f=None, fill=GREY1):
        f = f or font(14)
        cx, cy = x + w / 2, y + h / 2
        self.d.polygon([(cx * S, y * S), ((x + w) * S, cy * S), (cx * S, (y + h) * S), (x * S, cy * S)],
                       fill=fill, outline=BLACK, width=2 * S)
        self.center_text(x, y, w, h, lines, f)

    def save(self, name):
        w, h = self.im.size
        self.im.resize((w // 2, h // 2), Image.LANCZOS).save(os.path.join(OUT, name), dpi=(200, 200))
        # keep full-res too
        self.im.save(os.path.join(OUT, name), dpi=(300, 300))


# ───────────────────────── Figure: architecture ─────────────────────────
def fig_architecture():
    c = Canvas(900, 600)
    c.text(450, 24, "QuizMaster Pro - Layered Architecture", font(18, True), anchor="mm")

    # Presentation layer
    c.rect(30, 55, 840, 205, fill=GREY1)
    c.text(45, 62, "PRESENTATION LAYER  (com.quizmaster.view, components, util)", font(14, True))
    views = ["MainWindow\n(CardLayout)", "LoginPanel", "DashboardPanel", "CategorySelect\nPanel", "QuizPanel", "ResultPanel",
             "HistoryPanel", "SettingsPanel"]
    x0, y0 = 45, 95
    for i, v in enumerate(views):
        c.box(x0 + (i % 4) * 205, y0 + (i // 4) * 62, 190, 50, v.split("\n"), font(13), fill="white")
    c.text(45, 225, "Reusable components: RoundedCard, PrimaryButton, SecondaryButton, OptionCardButton, StatCard,", font(12))
    c.text(45, 241, "PillBadge, CircularScoreMeter, AuthInputField, SidebarNav   |   util: AppTheme, HtmlText, UiKit", font(12))

    # Domain layer
    c.rect(30, 300, 410, 120, fill=GREY2)
    c.text(45, 307, "DOMAIN / MODEL LAYER  (model)", font(14, True))
    for i, m in enumerate(["Question", "QuizSession", "QuizResult", "UserAccount", "UserProfile", "Category"]):
        c.box(45 + (i % 3) * 132, 335 + (i // 3) * 40, 122, 32, m, font(12), fill="white")

    # Service layer
    c.rect(460, 300, 410, 120, fill=GREY2)
    c.text(475, 307, "SERVICE LAYER  (service)", font(14, True))
    for i, m in enumerate(["StorageService", "QuestionBankService", "JsonParser", "AudioService"]):
        c.box(475 + (i % 2) * 195, 335 + (i // 2) * 40, 185, 32, m, font(12), fill="white")

    # Data
    c.rect(30, 470, 840, 110, fill=GREY1)
    c.text(45, 477, "PERSISTENCE  (flat JSON files in /data - no database server needed)", font(14, True))
    for i, m in enumerate(["questions/*.json\n(4 question banks)", "users/users.json\n(salted SHA-256 hashes)",
                           "users/user_<name>.json\n(profile & statistics)", "history/history_<name>.json\n(past attempts)"]):
        c.box(45 + i * 205, 510, 190, 55, m.split("\n"), font(12), fill="white")

    # arrows
    c.line([(235, 260), (235, 300)])
    c.line([(665, 260), (665, 300)])
    c.line([(665, 420), (665, 470)])
    c.line([(440, 360), (460, 360)], arrow=True)
    c.save("fig_architecture.png")


# ───────────────────────── Figure: quiz flow ─────────────────────────
def fig_flow():
    c = Canvas(900, 900)
    f = font(13)
    c.text(450, 20, "Quiz Session Flowchart", font(18, True), anchor="mm")

    def term(x, y, w, h, t):
        c.rect(x, y, w, h, fill=GREY2, r=h // 2)
        c.center_text(x, y, w, h, t, f)

    cx = 450
    term(350, 40, 200, 36, "START")
    c.line([(cx, 76), (cx, 100)])
    c.box(330, 100, 240, 42, ["Login / Register / Guest"], f)
    c.line([(cx, 142), (cx, 166)])
    c.diamond(340, 166, 220, 72, ["Credentials", "valid?"], f)
    c.line([(340, 202), (250, 202), (250, 121), (330, 121)])
    c.text(300, 186, "No", f)
    c.line([(cx, 238), (cx, 268)])
    c.text(460, 246, "Yes", f)
    c.box(330, 268, 240, 42, ["Dashboard (stats, activity)"], f)
    c.line([(cx, 310), (cx, 334)])
    c.box(330, 334, 240, 42, ["Choose category + question count"], f)
    c.line([(cx, 376), (cx, 400)])
    c.box(330, 400, 240, 42, ["Load and shuffle questions;", "start per-question timer"], f)
    c.line([(cx, 442), (cx, 466)])
    c.box(330, 466, 240, 42, ["Show question and 4 options"], f)
    c.line([(cx, 508), (cx, 532)])
    c.diamond(310, 532, 280, 84, ["User action?"], f)

    # left: lifeline loop
    c.line([(310, 574), (230, 574)])
    c.box(60, 552, 170, 44, ["50:50 or Hint", "(once per quiz)"], f)
    c.line([(145, 552), (145, 487), (330, 487)])

    # right: skip / timeout
    c.line([(590, 574), (690, 574)])
    c.box(690, 552, 170, 44, ["Skip / time-out", "(mark SKIPPED)"], f)

    # bottom: confirm
    c.line([(cx, 616), (cx, 650)])
    c.text(460, 626, "Confirm", f)
    c.box(330, 650, 240, 46, ["Show correct / wrong", "and explanation"], f)
    c.line([(cx, 696), (cx, 726)])
    c.diamond(340, 726, 220, 72, ["More", "questions?"], f)

    # skip joins the 'more questions' decision
    c.line([(775, 596), (775, 762), (560, 762)])

    # yes loops back to next question
    c.line([(340, 762), (30, 762), (30, 487), (330, 487)])
    c.text(300, 746, "Yes", f)

    # no: finish
    c.line([(cx, 798), (cx, 828)])
    c.text(460, 806, "No", f)
    c.box(310, 828, 280, 42, ["Save result, update profile,", "show score screen"], f)
    c.save("fig_flow.png")


# ───────────────────────── Figure: class diagram ─────────────────────────
def uml(c, x, y, w, name, attrs, ops, fill="white"):
    f = mono(13)
    lh = 18
    hh = 30
    h = hh + len(attrs) * lh + 10 + len(ops) * lh + 10
    c.rect(x, y, w, h, fill=fill, r=4)
    c.d.rectangle([x * S, y * S, (x + w) * S, (y + hh) * S], fill=GREY2, outline=BLACK, width=2 * S)
    c.text(x + w / 2, y + hh / 2, name, font(15, True), anchor="mm")
    yy = y + hh + 5
    for a in attrs:
        c.text(x + 8, yy, a, f)
        yy += lh
    c.d.line([(x * S, (yy + 2) * S), ((x + w) * S, (yy + 2) * S)], fill=BLACK, width=S)
    yy += 8
    for o in ops:
        c.text(x + 8, yy, o, f)
        yy += lh
    return (x, y, w, h)


def fig_class():
    c = Canvas(960, 640)
    c.text(480, 20, "Core Class Diagram (simplified)", font(18, True), anchor="mm")
    uml(c, 20, 45, 250, "MainWindow", ["-storageService", "-questionBank", "-currentSession"],
        ["+showScreen(card)", "-startQuiz(cat, n)", "-handleQuizFinished(r)", "-confirmLeaveQuiz()"])
    uml(c, 20, 245, 250, "QuizPanel", ["-session", "-secondsRemaining", "-answerConfirmed"],
        ["+startSession(s)", "-confirmAnswer()", "-useFiftyFifty()", "-useHint()", "+isQuizInProgress()"])
    uml(c, 350, 45, 260, "QuizSession", ["-questions: List<Question>", "-selectedAnswers: int[]", "-currentIndex",
                                         "-fiftyFiftyUsed, -hintUsed"],
        ["+submitAnswer(i): boolean", "+skipCurrent()", "+useFiftyFifty(): int[]", "+useHint(): String",
         "+buildResult(): QuizResult"])
    uml(c, 350, 285, 260, "Question", ["-questionText", "-options: String[4]", "-correctIndex", "-hint, -explanation"],
        ["+isCorrect(i): boolean"])
    uml(c, 680, 45, 260, "QuizResult", ["-category, -totalQuestions", "-correct, -wrong, -skipped",
                                        "-scorePercent, -grade", "-durationSeconds, -timestamp"],
        ["+getFormattedDuration()"])
    uml(c, 680, 235, 260, "UserProfile", ["-displayName, -totalQuizzes", "-bestScore, -averageScore",
                                          "-dayStreak, -lastQuizDate", "-timerPreference"],
        ["+recordQuiz(cat, score)"])
    uml(c, 680, 440, 260, "UserAccount", ["-username, -displayName", "-salt, -passwordHash"],
        ["+create(u, n, pw)", "+verifyPassword(pw)"])
    uml(c, 350, 440, 260, "StorageService", ["-currentAccount"],
        ["+authenticate(u, pw)", "+register(u, n, pw)", "+loadProfile()/saveProfile()",
         "+loadHistory()/saveResult()", "+clearHistory()"])
    uml(c, 20, 470, 250, "QuestionBankService", ["-categoryCache: Map"],
        ["+getCategories()", "+getShuffledQuestions(cat, n)", "+getQuestionCount(cat)"])

    # MainWindow owns QuizPanel
    c.line([(145, 221), (145, 245)])
    c.text(153, 224, "owns", font(13))
    # MainWindow creates QuizSession
    c.line([(270, 100), (350, 100)])
    c.text(283, 85, "creates", font(13))
    # QuizPanel drives QuizSession
    c.line([(270, 330), (310, 330), (310, 215), (350, 215)])
    c.text(272, 312, "uses", font(13))
    # QuizSession contains Questions
    c.line([(480, 257), (480, 285)])
    c.text(488, 262, "1..*", font(13))
    # QuizSession builds QuizResult
    c.line([(610, 90), (680, 90)])
    c.text(620, 75, "builds", font(13))
    # MainWindow records into UserProfile via StorageService; StorageService manages profile, account, results
    c.line([(610, 470), (645, 470), (645, 330), (680, 330)])
    c.text(552, 424, "", font(11))
    c.line([(610, 520), (680, 520)])
    c.text(622, 505, "auth", font(13))
    # QuestionBankService provides Questions
    c.line([(270, 520), (310, 520), (310, 360), (350, 360)])
    c.text(272, 505, "loads", font(13))
    c.save("fig_class.png")


# ───────────────────────── Figure: alternative designs ─────────────────────────
def fig_designs():
    c = Canvas(960, 420)
    c.text(480, 20, "Alternative Designs Considered", font(18, True), anchor="mm")
    cols = [
        ("Design A: Console application", ["User", "Console menu (Scanner)", "Quiz logic (Java)", "Plain .txt files"]),
        ("Design B: Swing GUI + JSON files", ["User", "Swing desktop UI (CardLayout)", "Quiz logic + services (Java)",
                                              "JSON files in /data"]),
        ("Design C: JavaFX + database", ["User", "JavaFX UI (FXML)", "Quiz logic + DAO layer", "MySQL / SQLite database"]),
    ]
    for i, (title, blocks) in enumerate(cols):
        x = 20 + i * 315
        fill = GREY2 if i == 1 else "white"
        c.rect(x, 45, 295, 350, fill=fill)
        c.text(x + 147, 68, title, font(14, True), anchor="mm")
        for j, b in enumerate(blocks):
            by = 95 + j * 75
            c.box(x + 30, by, 235, 48, [b], font(13), fill="white")
            if j < len(blocks) - 1:
                c.line([(x + 147, by + 48), (x + 147, by + 75)])
        if i == 1:
            c.text(x + 147, 380, "SELECTED DESIGN", font(12, True), anchor="mm")
    c.save("fig_designs.png")


# ───────────────────────── Figure: graphical abstract ─────────────────────────
def fig_graphical():
    shots = os.path.join(OUT, "shots")
    c = Canvas(960, 520)
    c.text(480, 22, "QuizMaster Pro - Graphical Abstract", font(18, True), anchor="mm")
    # Left: user & login
    names = [("s01_login.png", "1. Secure login"), ("s04_category.png", "2. Choose topic"),
             ("s07_quiz_answered.png", "3. Timed quiz + feedback"), ("s08_result.png", "4. Score & review")]
    w, h = 205, 137
    for i, (fn, cap) in enumerate(names):
        x = 20 + i * 235
        c.rect(x - 4, 55 - 4, w + 8, h + 8, fill="white", r=4)
        im = Image.open(os.path.join(shots, fn)).convert("RGB").resize((w * S, h * S), Image.LANCZOS)
        c.im.paste(im, (x * S, 55 * S))
        c.text(x + w / 2, 55 + h + 20, cap, font(13, True), anchor="mm")
        if i < 3:
            c.line([(x + w + 6, 55 + h / 2), (x + 235 - 8, 55 + h / 2)])
    # middle band: engine
    c.rect(20, 255, 920, 105, fill=GREY1)
    c.text(480, 270, "Java Application Core", font(14, True), anchor="mm")
    items = ["Question bank\n(4 topics, 60 MCQs)", "Timer + 50:50 +\nHints", "Instant scoring,\ngrade & streak", "Sound cues\n(javax.sound)"]
    for i, it in enumerate(items):
        c.box(40 + i * 225, 285, 205, 60, it.split("\n"), font(12), fill="white")
    # bottom: storage
    c.rect(20, 395, 920, 100, fill=GREY2)
    c.text(480, 410, "Local JSON Storage (no server required)", font(14, True), anchor="mm")
    items = ["Accounts\n(salted SHA-256)", "Profile &\nstatistics", "Quiz history\n(last 50 attempts)"]
    for i, it in enumerate(items):
        c.box(60 + i * 295, 430, 250, 52, it.split("\n"), font(12), fill="white")
    c.line([(480, 230), (480, 255)], arrow=True)
    c.line([(480, 360), (480, 395)], arrow=True)
    c.save("fig_graphical.png")


fig_architecture()
fig_flow()
fig_class()
fig_designs()
fig_graphical()
print("figures done")
