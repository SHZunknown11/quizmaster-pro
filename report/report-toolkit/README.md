# report-toolkit

Helpers used to build the Chandigarh University mini-project report (docx + pdf).

- lib.js            paragraph / heading / figure / table / contents helpers for the Node `docx` package
- build.example.js  the full QuizMaster Pro report - copy it and replace the content and the facts at the top
- paginate.py       docx -> pdf (LibreOffice) -> finds every heading/figure/table page -> pages.json (run build, paginate, build, paginate)
- make_figs.py      Pillow helpers for black-and-white diagrams (boxes, arrows, diamonds, UML boxes)

Setup in a scratch folder: `npm init -y && npm install docx`, then `pip install pymupdf pillow`.
LibreOffice path is set at the top of paginate.py (SOFFICE). These files expect logo_p10_51.png,
logic_results.txt and a shots/ folder next to them; see build.example.js for the names.
