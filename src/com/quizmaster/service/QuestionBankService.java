package com.quizmaster.service;

import com.quizmaster.model.Question;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Loads questions from JSON files in the data/questions/ directory.
 * Supports loading all questions, loading by category, and shuffling.
 */
public class QuestionBankService {

    private final Path questionsDir;
    private final Map<String, List<Question>> categoryCache = new LinkedHashMap<>();

    /**
     * @param path the questions directory itself (e.g. {@code data/questions}).
     *             BUG-09: the previous fragile path sniffing could resolve
     *             {@code questions/questions} and silently find no question banks.
     */
    public QuestionBankService(Path path) {
        this.questionsDir = path;
        loadAll();
    }

    /**
     * Scans the questions directory and loads all .json files.
     */
    @SuppressWarnings("unchecked")
    private void loadAll() {
        categoryCache.clear();

        if (!Files.isDirectory(questionsDir)) {
            System.err.println("Questions directory not found: " + questionsDir);
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(questionsDir, "*.json")) {
            for (Path file : stream) {
                String content = Files.readString(file, StandardCharsets.UTF_8);
                Map<String, Object> root = JsonParser.parseObject(content);
                String category = JsonParser.getString(root, "category", "Unknown");
                List<Object> questionsArr = JsonParser.getArray(root, "questions");

                List<Question> questions = new ArrayList<>();
                for (Object item : questionsArr) {
                    Map<String, Object> q = (Map<String, Object>) item;
                    String id = JsonParser.getString(q, "id", UUID.randomUUID().toString());
                    String diff = JsonParser.getString(q, "difficulty", "MEDIUM");
                    String text = JsonParser.getString(q, "questionText", "");
                    List<Object> optsList = JsonParser.getArray(q, "options");
                    String[] options = optsList.stream().map(Object::toString).toArray(String[]::new);
                    int correctIdx = JsonParser.getInt(q, "correctOptionIndex", 0);
                    String hint = JsonParser.getString(q, "hint", "");
                    String explanation = JsonParser.getString(q, "explanation", "");

                    questions.add(new Question(id, category, diff, text, options, correctIdx, hint, explanation));
                }

                categoryCache.put(category, questions);
            }
        } catch (IOException e) {
            System.err.println("Error loading question banks: " + e.getMessage());
        }
    }

    /**
     * Returns all available category names.
     */
    public List<String> getCategories() {
        return new ArrayList<>(categoryCache.keySet());
    }

    /**
     * Returns the question count for a given category.
     */
    public int getQuestionCount(String category) {
        List<Question> qs = categoryCache.get(category);
        return qs != null ? qs.size() : 0;
    }

    /**
     * Returns a shuffled subset of questions for a given category.
     * @param category the category name
     * @param count    max number of questions to return
     */
    public List<Question> getQuestions(String category, int count) {
        List<Question> all = categoryCache.getOrDefault(category, new ArrayList<>());
        List<Question> shuffled = new ArrayList<>(all);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    public List<Question> getShuffledQuestions(String category, int count) {
        return getQuestions(category, count);
    }

    /**
     * Returns all questions across all categories.
     */
    public List<Question> getAllQuestions() {
        List<Question> all = new ArrayList<>();
        for (List<Question> qs : categoryCache.values()) {
            all.addAll(qs);
        }
        return all;
    }
}
