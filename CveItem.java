package com.example.model;

public class CveItem {
    private String id;
    private String summary;
    private String published;
    private String modified;
    private String baseScore;
    private String cwe;

    public CveItem() {
    }

    public CveItem(String id, String summary, String published, String modified, String baseScore, String cwe) {
        this.id = id;
        this.summary = summary;
        this.published = published;
        this.modified = modified;
        this.baseScore = baseScore;
        this.cwe = cwe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(String baseScore) {
        this.baseScore = baseScore;
    }

    public String getCwe() {
        return cwe;
    }

    public void setCwe(String cwe) {
        this.cwe = cwe;
    }

    @Override
    public String toString() {
        return "CveItem{" +
                "id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", published='" + published + '\'' +
                ", modified='" + modified + '\'' +
                ", baseScore='" + baseScore + '\'' +
                ", cwe='" + cwe + '\'' +
                '}';
    }
}

