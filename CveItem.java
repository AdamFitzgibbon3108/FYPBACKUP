package com.example.model;

public class CveItem {
    private String id;
    private String summary;
    private String published;
    private String modifiedDate;
    private String baseScore;
    private String cwe;

    public CveItem() {
    }

    public CveItem(String id, String summary, String published, String modifiedDate, String baseScore, String cwe) {
        this.id = id;
        this.summary = summary;
        this.published = published;
        this.modifiedDate = modifiedDate;
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

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
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
                ", modifiedDate='" + modifiedDate + '\'' +
                ", baseScore='" + baseScore + '\'' +
                ", cwe='" + cwe + '\'' +
                '}';
    }
}
