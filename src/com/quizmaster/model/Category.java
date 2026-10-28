package com.quizmaster.model;

/**
 * Represents a quiz category with display metadata and file mapping.
 */
public class Category {

    private final String id;
    private final String title;
    private final String description;
    private final String iconSymbol;
    private final String jsonFile;

    public Category(String id, String title, String description, String iconSymbol, String jsonFile) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconSymbol = iconSymbol;
        this.jsonFile = jsonFile;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIconSymbol() { return iconSymbol; }
    public String getJsonFile() { return jsonFile; }

    @Override
    public String toString() {
        return title;
    }
}
