package com.rentalconnects.backend.dto;

public class DocumentDTO {
    private String id;
    private String name;
    private String url;
    private String category;
    private String updated;

    // No-args constructor
    public DocumentDTO() {
    }

    // All-args constructor
    public DocumentDTO(String id, String name, String url, String category, String updated) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
        this.updated = updated;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}