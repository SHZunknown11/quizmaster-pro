// Shared building blocks for the report (Chandigarh University mini-project format)
const fs = require('fs');
const path = require('path');
const D = require('docx');
const {
  Paragraph, TextRun, ImageRun, Table, TableRow, TableCell, WidthType, AlignmentType, BorderStyle,
  ShadingType, TabStopType, LeaderType, LevelFormat, HeadingLevel, VerticalAlign,
} = D;

const FONT = 'Times New Roman';
const TEXTW = 8666;            // A4 width 11906 - left 1800 - right 1440 (twips)
const HERE = __dirname;

// registries filled while the body is built (used for TOC / list of figures / list of tables)
const reg = { toc: [], figs: [], tabs: [], figNo: 0, tabNo: 0, numInstance: 0 };

// ───────────── inline markup: **bold**, *italic*, [[highlighted placeholder]] ─────────────
function runs(text, o = {}) {
  const out = [];
  const parts = String(text).split(/(\*\*[^*]+\*\*|\[\[[^\]]+\]\]|`[^`]+`|\*[^*]+\*)/);
  for (const part of parts) {
    if (!part) continue;
    if (part.startsWith('**')) out.push(new TextRun({ ...o, text: part.slice(2, -2), bold: true, font: o.font || FONT }));
    else if (part.startsWith('`')) out.push(new TextRun({ ...o, text: part.slice(1, -1), font: 'Courier New', size: Math.round((o.size || 24) * 0.85) }));
    else if (part.startsWith('[[')) out.push(new TextRun({ ...o, text: part.slice(2, -2), highlight: 'yellow', font: o.font || FONT }));
    else if (part.startsWith('*')) out.push(new TextRun({ ...o, text: part.slice(1, -1), italics: true, font: o.font || FONT }));
    else out.push(new TextRun({ ...o, text: part, font: o.font || FONT }));
  }
  return out;
}

// ───────────── paragraphs ─────────────
const P = (text, o = {}) => new Paragraph({
  alignment: o.align || AlignmentType.JUSTIFIED,
  spacing: { line: 360, before: o.before ?? 0, after: o.after ?? 120 },
  indent: o.indent,
  keepNext: o.keepNext,
  children: runs(text, { size: o.size || 24, bold: o.bold }),
});

const Pc = (text, o = {}) => P(text, { ...o, align: AlignmentType.CENTER });

const blank = (n = 1) => Array.from({ length: n }, () => new Paragraph({ children: [new TextRun({ text: '', font: FONT })] }));

function chapter(no, title) {
  reg.toc.push({ level: 0, label: `Chapter ${no}.`, title: toTitle(title), key: `chap:${no}`, find: title.toUpperCase() });
  return [
    new Paragraph({ pageBreakBefore: true, alignment: AlignmentType.CENTER, spacing: { before: 120, after: 60 },
      children: [new TextRun({ text: `CHAPTER ${no}.`, bold: true, size: 32, font: FONT })] }),
    new Paragraph({ heading: HeadingLevel.HEADING_1, alignment: AlignmentType.CENTER, spacing: { after: 240 },
      children: [new TextRun({ text: title.toUpperCase(), bold: true, size: 32, font: FONT })] }),
  ];
}

// numbered heading with hanging indent: "1.1.<tab>Title"
function H2(num, title) {
  reg.toc.push({ level: 1, label: num, title, key: `h:${num}`, find: `${num} ${title}` });
  return new Paragraph({
    heading: HeadingLevel.HEADING_2, keepNext: true,
    spacing: { before: 200, after: 120, line: 276 },
    indent: { left: 720, hanging: 720 },
    tabStops: [{ type: TabStopType.LEFT, position: 720 }],
    children: [new TextRun({ text: `${num}\t${title}`, bold: true, size: 28, font: FONT })],
  });
}

function H3(num, title) {
  reg.toc.push({ level: 2, label: num, title, key: `h:${num}`, find: `${num} ${title}` });
  return new Paragraph({
    heading: HeadingLevel.HEADING_3, keepNext: true,
    spacing: { before: 160, after: 100, line: 276 },
    indent: { left: 900, hanging: 900 },
    tabStops: [{ type: TabStopType.LEFT, position: 900 }],
    children: [new TextRun({ text: `${num}\t${title}`, bold: true, size: 24, font: FONT })],
  });
}

// unnumbered main heading used for REFERENCES / APPENDIX etc. (centered, 16pt bold)
function plainChapter(title, key, tocTitle) {
  reg.toc.push({ level: 0, label: '', title: tocTitle || toTitle(title), key, find: title.toUpperCase() });
  return new Paragraph({
    pageBreakBefore: true, heading: HeadingLevel.HEADING_1, alignment: AlignmentType.CENTER, spacing: { before: 120, after: 240 },
    children: [new TextRun({ text: title.toUpperCase(), bold: true, size: 32, font: FONT })],
  });
}

function frontHeading(title, key, inToc = true) {
  if (inToc) reg.toc.push({ level: 0, label: '', title: toTitle(title), key, find: title.toUpperCase(), front: true });
  return new Paragraph({
    pageBreakBefore: true, heading: HeadingLevel.HEADING_1, alignment: AlignmentType.CENTER, spacing: { before: 120, after: 240 },
    children: [new TextRun({ text: title.toUpperCase(), bold: true, size: 32, font: FONT })],
  });
}

function toTitle(s) {
  return s.toLowerCase().replace(/\b([a-z])/g, (m) => m.toUpperCase()).replace(/\b(And|Of|The|In|To|For|With)\b/g, (m) => m.toLowerCase())
    .replace(/^./, (m) => m.toUpperCase());
}

// ───────────── lists ─────────────
const bullet = (text, o = {}) => new Paragraph({
  numbering: { reference: 'bul', level: o.level || 0 },
  alignment: AlignmentType.JUSTIFIED, spacing: { line: 360, after: 60 },
  children: runs(text, { size: 24 }),
});

function numbered(items) {
  const inst = reg.numInstance++;
  return items.map((t) => new Paragraph({
    numbering: { reference: 'num', level: 0, instance: inst },
    alignment: AlignmentType.LEFT, spacing: { line: 360, after: 60 },
    children: runs(t, { size: 24 }),
  }));
}

// ───────────── code ─────────────
function code(lines) {
  return lines.map((ln, i) => new Paragraph({
    spacing: { line: 240, before: i === 0 ? 80 : 0, after: i === lines.length - 1 ? 160 : 0 },
    indent: { left: 240, right: 120 },
    shading: { type: ShadingType.CLEAR, fill: 'F2F2F2', color: 'auto' },
    keepLines: true, keepNext: i < lines.length - 1,
    children: [new TextRun({ text: ln === '' ? ' ' : ln, font: 'Courier New', size: 18 })],
  }));
}

// ───────────── figures ─────────────
function pngSize(file) {
  const b = fs.readFileSync(file);
  return { w: b.readUInt32BE(16), h: b.readUInt32BE(20) };
}

function figure(file, caption, widthPx = 520, opts = {}) {
  const full = path.isAbsolute(file) ? file : path.join(HERE, file);
  const { w, h } = pngSize(full);
  const width = widthPx, height = Math.round(widthPx * h / w);
  reg.figNo += 1;
  const no = reg.figNo;
  reg.figs.push({ no, caption, key: `fig:${no}`, front: !!opts.front });
  return [
    new Paragraph({
      alignment: AlignmentType.CENTER, keepNext: true, spacing: { before: 120, after: 60 },
      children: [new ImageRun({ type: 'png', data: fs.readFileSync(full), transformation: { width, height },
        altText: { title: caption, description: caption, name: `figure-${no}` } })],
    }),
    new Paragraph({
      alignment: AlignmentType.CENTER, spacing: { after: 200 },
      children: [new TextRun({ text: `Figure ${no}: ${caption}`, bold: true, size: 20, font: FONT })],
    }),
  ];
}

// ───────────── tables ─────────────
const thin = { style: BorderStyle.SINGLE, size: 4, color: '000000' };
const borders = { top: thin, bottom: thin, left: thin, right: thin };
const none = { style: BorderStyle.NONE, size: 0, color: 'FFFFFF' };
const noBorders = { top: none, bottom: none, left: none, right: none };

function cell(text, width, o = {}) {
  const lines = Array.isArray(text) ? text : [text];
  return new TableCell({
    width: { size: width, type: WidthType.DXA },
    borders: o.borders || borders,
    shading: o.fill ? { type: ShadingType.CLEAR, fill: o.fill, color: 'auto' } : undefined,
    margins: { top: 50, bottom: 50, left: 90, right: 90 },
    verticalAlign: o.vAlign || VerticalAlign.CENTER,
    columnSpan: o.span,
    children: lines.map((t) => new Paragraph({
      alignment: o.align || AlignmentType.LEFT, spacing: { line: 252, after: 20 }, keepNext: !!o.keepNext,
      children: runs(t, { size: o.size || 21, bold: o.bold }),
    })),
  });
}

// caption above, as required by the format guide ("Table 1: Caption")
function table(caption, headers, rows, widths, o = {}) {
  const total = widths.reduce((a, b) => a + b, 0);
  const out = [];
  if (caption) {
    reg.tabNo += 1;
    reg.tabs.push({ no: reg.tabNo, caption, key: `tab:${reg.tabNo}` });
    out.push(new Paragraph({
      keepNext: true, spacing: { before: 160, after: 80 }, alignment: AlignmentType.CENTER,
      children: [new TextRun({ text: `Table ${reg.tabNo}: ${caption}`, bold: true, size: 20, font: FONT })],
    }));
  }
  const trs = [];
  if (headers) {
    trs.push(new TableRow({
      tableHeader: true, cantSplit: true,
      children: headers.map((h, i) => cell(h, widths[i], { bold: true, fill: 'D9D9D9', align: AlignmentType.CENTER, keepNext: !!o.keep })),
    }));
  }
  rows.forEach((r, ri) => trs.push(new TableRow({
    cantSplit: true,
    children: r.map((c, i) => {
      const shade = o.shade && o.shade(ri, i, c);
      return cell(c, widths[i], { align: (o.align && o.align[i]) || AlignmentType.LEFT, fill: shade || undefined, keepNext: !!o.keep && ri < rows.length - 1 });
    }),
  })));
  out.push(new Table({ width: { size: total, type: WidthType.DXA }, columnWidths: widths, rows: trs }));
  out.push(new Paragraph({ spacing: { after: 160 }, children: [new TextRun({ text: '', font: FONT })] }));
  return out;
}

// ───────────── contents lines ─────────────
function tocLine(label, title, page, indent = 0, o = {}) {
  const labelW = label ? (o.labelW || 700) : 0;
  return new Paragraph({
    spacing: { line: 360, after: 0 },
    indent: { left: indent + labelW, hanging: labelW },
    tabStops: [
      ...(label ? [{ type: TabStopType.LEFT, position: indent + labelW }] : []),
      { type: TabStopType.RIGHT, position: TEXTW, leader: LeaderType.DOT },
    ],
    children: [new TextRun({ text: `${label ? label + '\t' : ''}${title}\t${page}`, font: FONT, size: 24, bold: o.bold })],
  });
}

module.exports = {
  D, FONT, TEXTW, reg, runs, P, Pc, blank, chapter, H2, H3, plainChapter, frontHeading, toTitle, bullet, numbered, code,
  figure, table, cell, tocLine, borders, noBorders, none, HERE,
};
