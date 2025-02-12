package com.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", nullable = false)
    private String text;

    @Column(name = "category")
    private String category;

    @ManyToOne
    @JoinColumn(name = "control_category", referencedColumnName = "id")
    private SecurityControl controlCategory;

    @Column(name = "framework")
    private String framework;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "score")
    private Integer score;

    @Column(name = "role", nullable = false)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType; // Enum: TRUE_FALSE, MULTIPLE_CHOICE

    // Constructors
    public Question() {}

    public Question(String text, String category, SecurityControl controlCategory, String framework, String difficulty, Integer score, String role, QuestionType questionType) {
        this.text = text;
        this.category = category;
        this.controlCategory = controlCategory;
        this.framework = framework;
        this.difficulty = difficulty;
        this.score = score;
        this.role = role;
        this.questionType = questionType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public SecurityControl getControlCategory() {
        return controlCategory;
    }

    public void setControlCategory(SecurityControl controlCategory) {
        this.controlCategory = controlCategory;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
}

