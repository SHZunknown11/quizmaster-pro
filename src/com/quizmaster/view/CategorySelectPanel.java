package com.quizmaster.view;

import com.quizmaster.components.PrimaryButton;
import com.quizmaster.components.RoundedCard;
import com.quizmaster.components.SecondaryButton;
import com.quizmaster.model.Category;
import com.quizmaster.service.AudioService;
import com.quizmaster.service.QuestionBankService;
import com.quizmaster.util.AppTheme;
import com.quizmaster.util.UiKit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Category & Topic Selection Screen.
 * Allows the user to select one of the Java categories, pick question count,
 * and launch a quiz session.
 */
public class CategorySelectPanel extends JPanel {

    private final QuestionBankService questionBank;
    private final BiConsumer<String, Integer> onStartQuizSession;
    private final Runnable onBack;

    private String selectedCategory = "Java Fundamentals";
    private int selectedQuestionCount = 10;
    private JPanel categoryGrid;
    public CategorySelectPanel(QuestionBankService questionBank,
                               BiConsumer<String, Integer> onStartQuizSession,
                               Runnable onBack) {
        this.questionBank = questionBank;
        this.onStartQuizSession = onStartQuizSession;
        this.onBack = onBack;

        setBackground(AppTheme.BG_CANVAS);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(28, 36, 28, 36));

        initUI();
    }

    private void initUI() {
        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setOpaque(false);

        JLabel title = new JLabel("Choose a Quiz Category");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        titles.add(title);

        JLabel subtitle = new JLabel("Select a topic to test and level up your Java skills");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        titles.add(subtitle);

        header.add(titles, BorderLayout.WEST);

        SecondaryButton backBtn = new SecondaryButton("← Back");
        backBtn.setPreferredSize(new Dimension(100, 38));
        backBtn.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });
        header.add(UiKit.verticallyCentered(backBtn), BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center: 2x2 Category Cards
        categoryGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        categoryGrid.setOpaque(false);
        categoryGrid.setBorder(new EmptyBorder(24, 0, 24, 0));

        populateCategoryCards();
        add(categoryGrid, BorderLayout.CENTER);

        // Bottom Bar: Question Count Selector + Launch Button
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Question count options: [5 Qs] [10 Qs] [15 Qs]
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        countPanel.setOpaque(false);

        JLabel countLabel = new JLabel("Question Count: ");
        countLabel.setFont(AppTheme.FONT_BODY_BOLD);
        countLabel.setForeground(AppTheme.TEXT_SECONDARY);
        countPanel.add(countLabel);

        int[] counts = {5, 10, 15};
        ButtonGroup bg = new ButtonGroup();
        for (int c : counts) {
            JToggleButton tb = createCountButton(c);
            if (c == selectedQuestionCount) {
                tb.setSelected(true);
            }
            bg.add(tb);
            countPanel.add(tb);
        }
        bottomBar.add(countPanel, BorderLayout.WEST);

        PrimaryButton launchBtn = new PrimaryButton("Start Quiz →");
        launchBtn.setPreferredSize(new Dimension(180, 46));
        launchBtn.addActionListener(e -> {
            if (onStartQuizSession != null) {
                onStartQuizSession.accept(selectedCategory, selectedQuestionCount);
            }
        });
        bottomBar.add(launchBtn, BorderLayout.EAST);

        add(bottomBar, BorderLayout.SOUTH);

        // Make sure the initial highlight matches a category that really exists
        reset();
    }

    private JToggleButton createCountButton(int count) {
        JToggleButton btn = new JToggleButton(count + " Qs") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                AppTheme.applyQualityRendering(g2);
                int w = getWidth();
                int h = getHeight();

                boolean sel = isSelected();
                g2.setColor(sel ? AppTheme.ACCENT_AMBER : AppTheme.BG_CARD);
                g2.fillRoundRect(0, 0, w, h, 10, 10);

                g2.setColor(sel ? AppTheme.ACCENT_AMBER : AppTheme.BORDER_SUBTLE);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);

                g2.setFont(AppTheme.FONT_SMALL_BOLD);
                g2.setColor(sel ? AppTheme.TEXT_DARK : AppTheme.TEXT_PRIMARY);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);

                g2.dispose();
            }
        };
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(75, 34));
        btn.addActionListener(e -> {
            selectedQuestionCount = count;
            AudioService.playClick();
        });
        return btn;
    }

    private void populateCategoryCards() {
        categoryGrid.removeAll();
        for (String cat : resolveCategories()) {
            categoryGrid.add(createCategoryCard(cat));
        }
    }

    /**
     * The categories actually rendered as cards (falls back to the default set
     * when the question bank could not be read).
     */
    private List<String> resolveCategories() {
        List<String> categories = questionBank.getCategories();
        if (categories.isEmpty()) {
            categories = List.of("Java Fundamentals", "OOP Concepts", "Collections Framework", "Data Structures");
        }
        return categories;
    }

    /**
     * BUG-10: resets the highlighted category back to the first available card so
     * a stale selection from a previous visit is never shown.
     */
    public void reset() {
        List<String> categories = resolveCategories();
        if (!categories.isEmpty()) {
            selectedCategory = categories.get(0);
        }
        categoryGrid.revalidate();
        categoryGrid.repaint();
    }

    private JPanel createCategoryCard(String catName) {
        RoundedCard card = new RoundedCard(AppTheme.CARD_RADIUS, AppTheme.BG_CARD, AppTheme.BORDER_SUBTLE, 1) {
            @Override
            protected void paintComponent(Graphics g) {
                boolean isSel = catName.equalsIgnoreCase(selectedCategory);
                if (isSel) {
                    setBorderColor(AppTheme.ACCENT_AMBER);
                    setBackgroundColor(AppTheme.BG_CARD_HOVER);
                } else {
                    setBorderColor(AppTheme.BORDER_SUBTLE);
                    setBackgroundColor(AppTheme.BG_CARD);
                }
                super.paintComponent(g);
            }
        };

        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 22, 20, 22));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedCategory = catName;
                AudioService.playClick();
                categoryGrid.repaint();
            }
        });

        // Content
        JPanel body = new JPanel(new GridLayout(3, 1, 0, 4));
        body.setOpaque(false);

        JLabel nameLabel = new JLabel(catName);
        nameLabel.setFont(AppTheme.FONT_HEADING);
        nameLabel.setForeground(AppTheme.TEXT_PRIMARY);
        body.add(nameLabel);

        int qCount = questionBank.getQuestionCount(catName);
        JLabel metaLabel = new JLabel(qCount + " Curated Questions Available");
        metaLabel.setFont(AppTheme.FONT_SMALL);
        metaLabel.setForeground(AppTheme.ACCENT_AMBER);
        body.add(metaLabel);

        String desc = getCategoryDescription(catName);
        JLabel descLabel = new JLabel("<html><body style='width: 250px; color: #94A3B8;'>" + desc + "</body></html>");
        descLabel.setFont(AppTheme.FONT_SMALL);
        body.add(descLabel);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private String getCategoryDescription(String cat) {
        if (cat.contains("Fundamentals")) return "Keywords, primitives, control flow, methods, and syntax.";
        if (cat.contains("OOP")) return "Inheritance, encapsulation, polymorphism, and abstraction.";
        if (cat.contains("Collections")) return "Lists, sets, maps, iterators, hashing, and algorithms.";
        if (cat.contains("Data Structures")) return "Arrays, linked lists, trees, heaps, and complexity.";
        return "Comprehensive practice test covering core topics.";
    }
}
