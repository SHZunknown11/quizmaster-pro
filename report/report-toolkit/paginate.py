import json, os, re, subprocess, sys
import pymupdf

HERE = os.path.dirname(os.path.abspath(__file__))
SOFFICE = r"C:\Program Files\LibreOffice\program\soffice.exe"
DOCX = os.path.join(HERE, "Project_Report.docx")
PDF = os.path.join(HERE, "Project_Report.pdf")


def to_pdf():
    if os.path.exists(PDF):
        os.remove(PDF)
    subprocess.run([SOFFICE, "--headless", "--convert-to", "pdf", "--outdir", HERE, DOCX],
                   check=True, capture_output=True, timeout=300)


def norm(s):
    return re.sub(r"\s+", " ", s.replace("\u2019", "'")).strip()


def roman(n):
    vals = [(10, "x"), (9, "ix"), (5, "v"), (4, "iv"), (1, "i")]
    out = ""
    for v, r in vals:
        while n >= v:
            out += r
            n -= v
    return out


def locate():
    reg = json.load(open(os.path.join(HERE, "registry.json"), encoding="utf-8"))
    doc = pymupdf.open(PDF)
    texts = [re.sub(r"^(\d+|[ivx]+) ", "", norm(p.get_text())) for p in doc]
    n = len(texts)

    # chapter 1 start: first page after the front matter containing "CHAPTER 1." and "INTRODUCTION"
    ch1 = next(i for i, t in enumerate(texts) if t.startswith("CHAPTER 1. INTRODUCTION"))
    pages = {}

    def arabic(i):
        return str(i - ch1 + 1)

    # ---- front matter (uppercase headings, searched before chapter 1) ----
    front = {"front:ack": "ACKNOWLEDGEMENT", "front:toc": "TABLE OF CONTENTS", "front:lof": "LIST OF FIGURES",
             "front:lot": "LIST OF TABLES", "front:abs": "ABSTRACT", "front:gabs": "GRAPHICAL ABSTRACT",
             "front:abb": "ABBREVIATIONS", "front:sym": "SYMBOLS"}
    first_front = 2  # title + bonafide are not numbered
    for key, head in front.items():
        for i in range(first_front, ch1):
            if texts[i].startswith(head):
                pages[key] = roman(i - first_front + 1)
                break
    # ---- main-body headings in document order ----
    ptr = ch1
    for e in reg["toc"]:
        if e.get("front"):
            continue
        key, find = e["key"], norm(e["find"])[:38]
        for i in range(ptr, n):
            if find in texts[i]:
                pages[key] = arabic(i)
                ptr = i
                break
        else:
            print("NOT FOUND heading:", key, find)
    # ---- figures / tables ----
    for kind in ("figs", "tabs"):
        ptr = ch1
        for e in reg[kind]:
            if e.get("front"):
                pages[e["key"]] = pages.get("front:gabs", "0")
                continue
            label = ("Figure" if kind == "figs" else "Table") + f" {e['no']}: " + norm(e["caption"])[:28]
            for i in range(ptr, n):
                if label in texts[i]:
                    pages[e["key"]] = arabic(i)
                    ptr = i
                    break
            else:
                print("NOT FOUND caption:", label)
    json.dump(pages, open(os.path.join(HERE, "pages.json"), "w"), indent=1)
    return n, ch1


if __name__ == "__main__":
    to_pdf()
    n, ch1 = locate()
    print("pdf pages:", n, "chapter1 at pdf page", ch1 + 1)
