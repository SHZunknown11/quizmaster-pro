package com.quizmaster.util;

import java.awt.*;

/**
 * Centralized design system for QuizMaster Pro.
 * All colors, fonts, spacing, and rendering utilities live here.
 * No component should hardcode visual constants — always reference AppTheme.
 */
public final class AppTheme {

    private AppTheme() {} // non-instantiable utility class

    // ── Canvas & Surfaces ──────────────────────────────────────────
    public static final Color BG_CANVAS       = new Color(0x0F172A); // Deep Midnight Slate
    public static final Color BG_CARD         = new Color(0x1E293B); // Card Background
    public static final Color BG_CARD_HOVER   = new Color(0x27354A); // Elevated Hover
    public static final Color BG_SURFACE_DARK = new Color(0x0B0F17); // Deepest Sidebar
    public static final Color BG_INPUT        = new Color(0x162032); // Input field bg

    // ── Borders ────────────────────────────────────────────────────
    public static final Color BORDER_SUBTLE   = new Color(0x334155); // 1px Hairline
    public static final Color BORDER_ACTIVE   = new Color(0xF59E0B); // Active Amber

    // ── Primary Accents ────────────────────────────────────────────
    public static final Color ACCENT_AMBER       = new Color(0xF59E0B); // Warm Golden Amber
    public static final Color ACCENT_AMBER_HOVER = new Color(0xD97706); // Amber Hover
    public static final Color ACCENT_AMBER_DIM   = new Color(0xF59E0B).darker(); // Amber for text on dark
    public static final Color ACCENT_BLUE        = new Color(0x38BDF8); // Sky Cyan
    public static final Color ACCENT_GREEN       = new Color(0x10B981); // Emerald Correct
    public static final Color ACCENT_RED         = new Color(0xEF4444); // Crimson Incorrect
    public static final Color ACCENT_SLATE       = new Color(0x64748B); // Muted Skipped

    // ── Text Hierarchy ─────────────────────────────────────────────
    public static final Color TEXT_PRIMARY   = new Color(0xF8FAFC); // Pure Crisp White
    public static final Color TEXT_SECONDARY = new Color(0x94A3B8); // Cool Muted Slate
    public static final Color TEXT_MUTED     = new Color(0x64748B); // Low Priority
    public static final Color TEXT_DARK      = new Color(0x0F172A); // Dark text on light bg

    // ── Fonts ──────────────────────────────────────────────────────
    private static final String FONT_FAMILY      = "Segoe UI";
    private static final String FONT_FAMILY_MONO = "Consolas";

    public static final Font FONT_TITLE      = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font FONT_HEADING    = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY       = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD  = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_SMALL      = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font(FONT_FAMILY, Font.BOLD, 12);
    public static final Font FONT_CODE       = new Font(FONT_FAMILY_MONO, Font.PLAIN, 13);
    public static final Font FONT_KPI_NUM    = new Font(FONT_FAMILY, Font.BOLD, 32);
    public static final Font FONT_SCORE_BIG  = new Font(FONT_FAMILY, Font.BOLD, 42);

    // ── Spacing & Metrics ──────────────────────────────────────────
    public static final int SIDEBAR_WIDTH    = 72;
    public static final int CARD_RADIUS      = 16;
    public static final int CARD_PADDING     = 20;
    public static final int BUTTON_RADIUS    = 12;
    public static final int PILL_RADIUS      = 20;
    public static final int SPACING_XS       = 4;
    public static final int SPACING_SM       = 8;
    public static final int SPACING_MD       = 16;
    public static final int SPACING_LG       = 24;
    public static final int SPACING_XL       = 32;

    // ── Window ─────────────────────────────────────────────────────
    public static final int WINDOW_WIDTH  = 960;
    public static final int WINDOW_HEIGHT = 680;

    /**
     * Applies high-quality anti-aliased rendering hints to a Graphics2D context.
     * Call this at the top of every paintComponent override.
     */
    public static void applyQualityRendering(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /**
     * Creates a slightly brighter version of the given color for hover states.
     */
    public static Color brighten(Color c, float factor) {
        int r = Math.min(255, (int)(c.getRed() + (255 - c.getRed()) * factor));
        int g = Math.min(255, (int)(c.getGreen() + (255 - c.getGreen()) * factor));
        int b = Math.min(255, (int)(c.getBlue() + (255 - c.getBlue()) * factor));
        return new Color(r, g, b, c.getAlpha());
    }

    /**
     * Creates a color with modified alpha.
     */
    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    /**
     * Calculates the grade string from a percentage score.
     */
    public static String gradeFromPercent(int percent) {
        if (percent >= 95) return "A+";
        if (percent >= 80) return "A";
        if (percent >= 65) return "B";
        if (percent >= 50) return "C";
        return "F";
    }

    /**
     * Returns a greeting based on current time of day.
     */
    public static String timeOfDayGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }
}
