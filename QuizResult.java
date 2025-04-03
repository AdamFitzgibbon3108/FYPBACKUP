package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private int totalScore;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private Double scorePercentage;

    @Column(nullable = false)
    private Boolean passed;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    public QuizResult() {}

    public QuizResult(User user, int totalScore, int totalQuestions, String category, String role) {
        this.user = user;
        this.totalScore = totalScore;
        this.totalQuestions = totalQuestions;
        this.category = category;
        this.role = role;
        this.completedAt = LocalDateTime.now();
        this.scorePercentage = calculatePercentage(totalScore, totalQuestions);
        this.passed = this.scorePercentage >= 80;
        this.recommendations = null; 
    }

    private double calculatePercentage(int score, int questions) {
        return questions == 0 ? 0.0 : ((double) score / questions) * 100.0;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
        updateScoreData();
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
        updateScoreData();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public double getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(double scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    private void updateScoreData() {
        this.scorePercentage = calculatePercentage(this.totalScore, this.totalQuestions);
        this.passed = this.scorePercentage >= 80;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", totalScore=" + totalScore +
                ", totalQuestions=" + totalQuestions +
                ", scorePercentage=" + scorePercentage +
                ", passed=" + passed +
                ", category='" + category + '\'' +
                ", role='" + role + '\'' +
                ", completedAt=" + completedAt +
                ", recommendations='" + recommendations + '\'' +
                '}';
    }
}


