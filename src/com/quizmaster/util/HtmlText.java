package com.quizmaster.util;

import java.awt.HeadlessException;
import java.awt.Toolkit;

/**
 * Helpers for putting user/question text into Swing HTML labels safely.
 * Swing treats label text starting with {@code <html>} as markup, so raw text such as
 * {@code List<String>} would silently lose its generic type unless it is escaped.
 */
public final class HtmlText {

    private HtmlText() {}

    /** Escapes markup characters and turns line breaks into {@code <br>}. */
    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\r\n", "\n")
                .replace("\n", "<br>");
    }

    /**
     * Wraps already-safe HTML so it wraps at the given width in component pixels.
     * Swing scales CSS {@code px} by the screen DPI (120 dpi on a 125% display), so a
     * fixed "width: 580px" would wrap far wider than the card it sits in; the width is
     * converted back to CSS pixels here.
     */
    public static String html(String safeBody, int widthPx) {
        int cssWidth = Math.round(widthPx * 96f / screenDpi());
        return "<html><body style='width: " + cssWidth + "px;'>" + safeBody + "</body></html>";
    }

    private static int screenDpi() {
        try {
            int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            return dpi > 0 ? dpi : 96;
        } catch (HeadlessException e) {
            return 96;
        }
    }

    /** Escapes plain text and wraps it at the given pixel width. */
    public static String paragraph(String plain, int widthPx) {
        return html(escape(plain), widthPx);
    }
}
