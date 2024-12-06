package edu.asu.DatabasePart1;

public class Article {
    private String id;
    private String level;
    private String title;
    private String authors;
    private String setOfKeywords;
    private String keywords;
    private String body;
    private String referencesColumn;
    private String groupOfArticles;

    public Article(String id, String level, String title, String authors, String setOfKeywords, String keywords, String body, String referencesColumn, String groupOfArticles) {
        this.id = id;
        this.level = level;
        this.title = title;
        this.authors = authors;
        this.setOfKeywords = setOfKeywords;
        this.keywords = keywords;
        this.body = body;
        this.referencesColumn = referencesColumn;
        this.groupOfArticles = groupOfArticles;
    }

    public String getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getSetOfKeywords() {
        return setOfKeywords;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getBody() {
        return body;
    }

    public String getReferencesColumn() {
        return referencesColumn;
    }

    public String getGroupOfArticles() {
        return groupOfArticles;
    }
}