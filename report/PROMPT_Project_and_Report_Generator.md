# Reusable prompt: build a mini-project AND its university report

How to use: open Claude Code in an EMPTY folder, copy everything from the line
"=== PROMPT START ===" to "=== PROMPT END ===" into the chat, and fill in the
`[ ... ]` fields first. Put the university's format PDF (and the logo, if you have a
better one) in that folder before you start.

Optional speed-up: copy the `report-toolkit` folder (next to this file) into your
folder. The prompt tells Claude to reuse it.

=== PROMPT START ===

You are my project engineer and technical writer. Do the whole job below in order, do
not stop to ask me things I have already given you, and do not invent results.

## 0. My details (use these exactly; anything marked [ ] that I left empty -> ask me ONCE, together, before starting)

PROJECT TITLE: [ e.g. "Library Management System" ]
COURSE / SUBJECT + CODE: [ e.g. Project Based Learning in Java, 24CSP-301 ]
LANGUAGE / STACK: [ e.g. Java Swing, no external libraries ]   (if empty: choose the simplest stack that fits the course and say why)
DEGREE + BRANCH: [ e.g. Bachelor of Engineering in Computer Science and Engineering ]
SEMESTER / SECTION-GROUP: [ e.g. 5th / 719(A) ]
SUBMISSION MONTH-YEAR: [ e.g. Oct-2026 ]
TEAM MEMBERS (name + UID, one per line): [ ... ]
SUPERVISOR: name [ ... ]   designation [ e.g. Assistant Professor ]   department [ ... ]
HEAD OF DEPARTMENT: name [ ... ]   department [ ... ]
UNIVERSITY + FULL ADDRESS: [ e.g. Chandigarh University, NH-05, Chandigarh-Ludhiana Highway, Mohali, Punjab - 140413 ]
VIVA DATE: [ leave empty if unknown ]
WHO DID WHAT (per member): [ ... or "unknown" ]
REAL PROJECT START / END DATES (for the Gantt chart): [ ... or "estimate it" ]
THE UNIVERSITY'S FORMAT PDF: [ file name in this folder ]
OUTPUT PREFERENCE: .docx (editable) + .pdf (print), saved in a folder called `report/`.

## 1. Read the format guide first

Open the format PDF (use PyMuPDF: `pymupdf.open(...)`, `page.get_text()` and
`page.get_pixmap(dpi=95).save(...)` to LOOK at the pages as images - text extraction
drops margins, bullets and table content). Extract and write down every rule:
page size, fonts and sizes for body / chapter / heading / sub-heading / captions,
line spacing, order of sections, what the title page and certificate must contain,
page-number style (roman for front matter, arabic from Chapter 1), whether
Abstract / Graphical Abstract / Abbreviations / Symbols / Gantt chart / alternative
designs / user manual / achievements are required, and the reference style.
Extract the university logo from the PDF with `page.get_images()` -> `pymupdf.Pixmap`.
Show me the rule list in a short table, then follow it exactly.

## 2. Design and BUILD a real, complete project from the title

- Write a one-paragraph problem statement and a feature list (Must / Should / Could).
- Choose the architecture (layered / MVC), define the data model and storage.
- Implement the project fully. It must compile and run from a clean checkout with a
  documented build + run command (and `build.bat` / `run.bat` on Windows).
- Quality bar: no placeholder code, no dead buttons, input validation, sensible error
  messages, passwords hashed with salt if there are accounts, no secrets committed,
  a `.gitignore`, a `README.md`, and at least ~40 realistic seed records / questions
  if the project needs data.
- If it has a GUI: consistent theme, readable contrast, keyboard access, no emoji or
  special glyphs that the UI font may lack (render check below), and do NOT put raw
  user text into HTML labels without escaping it.
- Use Git. Commit in logical steps. Do not push anything to GitHub unless I say so.

## 3. Verify it honestly (this is what the report will quote)

1. Write automated logic tests for the model/service layer (grading/calculation rules,
   validation, persistence round-trips, security checks). Run them. Keep the raw output.
2. If there is a GUI: render EVERY screen off-screen to PNG at the smallest supported
   window size and LOOK at each image. Fix clipped text, unreadable colours, missing
   glyphs, overlapping elements. Then drive the real window with a small script for the
   key flows (login, main task, confirmation dialogs, data reset) and keep the output.
3. Clone/copy the project into an empty folder and build + run it from scratch.
4. Keep a list of every defect you found and fixed (ID, type, defect, correction).
Only numbers that come from these runs may appear in the report.

## 4. Produce the figures

Black-and-white friendly (the report is printed): architecture diagram, flowchart of
the main process, class/ER diagram, block diagrams of THREE alternative designs
(mark the chosen one), graphical abstract (screenshots + core + storage), and real
screenshots of every screen. Draw with Pillow (no extra installs) at 2x and downscale.
View each PNG after drawing it and fix messy connectors or tiny text.

## 5. Write and build the report (.docx first, then .pdf)

Tools: Node `docx` (install it in a scratch folder: `npm init -y && npm install docx`),
LibreOffice (`soffice --headless --convert-to pdf`), PyMuPDF to render pages.
Reuse `report-toolkit/lib.js`, `paginate.py`, `make_figs.py` if present; use
`report-toolkit/build.example.js` as the pattern for chapter content.

Structure (follow the format guide's order; adjust only if the guide says otherwise):
Title page -> Bonafide certificate (logo, supervisor name then the word SUPERVISOR in
capitals, designation, department, full institution address, HoD block, viva line,
internal/external examiner) -> Acknowledgement -> Table of Contents -> List of Figures
-> List of Tables -> Abstract (ONE page, 14 pt, double spaced) -> Graphical Abstract ->
Abbreviations -> Symbols -> Chapter 1 Introduction (client/need/contemporary issue,
problem - with no hint of the solution, tasks, timeline as a Gantt TABLE, organisation
of the report) -> Chapter 2 Design flow (feature evaluation table, design constraints
table covering regulations/economic/environmental/health/manufacturability/safety/
professional/ethical/social/political, feature finalisation, 3 alternative designs,
weighted comparison table with the arithmetic computed in code, chosen design and
why, architecture, flowchart, class diagram, key algorithms, data design) ->
Chapter 3 Results and validation (tools table, every screen as a figure, testing
method, the REAL test results, defects found and fixed, interpretation) ->
Chapter 4 Conclusion (expected vs achieved, honest deviations) and Future work ->
References -> Appendices (structure and data samples, code excerpts, full test list,
repository link) -> User manual (step by step, with pictures, troubleshooting) ->
Achievements.

Formatting rules that bit us before:
- A4. Body 12 pt, 1.5 spacing, justified. Chapter titles 16 pt bold centred
  ("CHAPTER 1." then the title on the next line). Headings 14 pt bold with numbers
  ("1.1."). Sub-headings 12 pt bold ("1.1.1."). Captions 10 pt bold: "Figure N: ..."
  BELOW figures, "Table N: ..." ABOVE tables. Tables single-spaced, 10.5 pt.
- Three Word sections: (1) title + certificate, no page number; (2) front matter with
  lower-roman numbers starting at i; (3) main matter with arabic numbers starting at 1.
- Contents, list of figures and list of tables are typed, with dot leaders (right tab
  stop with `LeaderType.DOT`). Get the real page numbers by TWO PASSES: build -> convert
  to PDF -> locate every heading/caption with PyMuPDF (strip the footer page number
  that PyMuPDF puts at the start of each page's text) -> write `pages.json` -> rebuild ->
  repeat until `pages.json` stops changing. Front-matter headings are typed in capitals so
  they cannot be confused with the contents lines.
- Keep small tables together (`keepNext` on every cell paragraph except the last row);
  let long tables split with a repeated header row.
- Do not justify list items that contain inline code. Keep code lines under ~76
  characters in 9 pt Courier or they wrap.
- Use plain ASCII hyphens and the characters Times New Roman has; draw check marks etc.
  as images.
- Swing/HTML only: CSS pixel widths scale with the screen DPI - convert them.
- Anything I must fill in or decide (HoD name, viva date, achievements, statistics,
  team roles, estimated dates) is highlighted YELLOW in the .docx with a short hint.
- References: only real, verifiable sources (official docs, standards, well-known
  books). Add the note "verify each reference" in your final message.
- Write in clear, plain English in the first person plural ("we"). No filler, no
  invented statistics, no invented survey results, no claims about testing you did not run.

## 6. Disclosure and honesty (do this, do not skip it)

- Add a row to the tools table: "AI assistance: <tool name> - used for <what you
  actually did>. Reviewed by the authors." and highlight it so we can check it against
  our university's AI policy.
- Tell me which parts of the work I should be able to explain at the viva (architecture,
  the main algorithm, the data model, how to run the tests) and give a 10-question
  viva cheat-sheet in `report/viva_questions.md`.

## 7. Verify before you hand over

Render the final PDF to images and look at ALL pages (contact sheets of 8 pages are
fine): no clipped tables, no orphan headings, no blank pages, figures readable at
print size, page numbers in the contents match the pages, the abstract fits one page,
placeholders visible. Fix and rebuild until clean.

## 8. Deliver

Save to `report/`: `<Project_Name>_Project_Report.docx` and `.pdf`, plus
`viva_questions.md`. Finish with a SHORT message: where the files are, the yellow
items I still must fill in, which numbers in the report came from which test run,
and anything you were unsure about. Do not commit or push unless I ask.

=== PROMPT END ===

## Notes for whoever uses this

- It works best when you give the real supervisor/HoD details up front; otherwise
  those stay yellow.
- A good project title is specific ("Hostel Complaint Tracker with role-based login"
  beats "Management System"). If your title is vague, Claude will pick a scope and
  tell you - read it.
- Time: a full run (project + tests + 40-page report) is long. Let it finish; check the
  yellow items and the viva sheet at the end.
- You are still the author. Read the report, run the project yourself, and be ready to
  explain it. Follow your university's rules about AI assistance and disclose it where
  they require it.
