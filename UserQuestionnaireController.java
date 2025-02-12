package com.example.controller;

import com.example.model.UserQuestionnaire;
import com.example.service.UserQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-questionnaires")
public class UserQuestionnaireController {

    @Autowired
    private UserQuestionnaireService userQuestionnaireService;

    /**
     * Create a new questionnaire for the authenticated user.
     */
    @PostMapping("/create")
    public UserQuestionnaire createUserQuestionnaire() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userQuestionnaireService.createUserQuestionnaire(username);
    }

    /**
     * Add a question to a user's questionnaire.
     */
    @PostMapping("/{questionnaireId}/add-question/{questionId}")
    public String addQuestionToQuestionnaire(@PathVariable Long questionnaireId, @PathVariable Long questionId) {
        userQuestionnaireService.addQuestionToUserQuestionnaire(questionnaireId, questionId);
        return "Question added successfully!";
    }

    /**
     * Remove a question from a user's questionnaire.
     */
    @DeleteMapping("/{questionnaireId}/remove-question/{questionId}")
    public String removeQuestionFromQuestionnaire(@PathVariable Long questionnaireId, @PathVariable Long questionId) {
        userQuestionnaireService.removeQuestionFromUserQuestionnaire(questionnaireId, questionId);
        return "Question removed successfully!";
    }

    /**
     * Retrieve all questionnaires for the authenticated user.
     */
    @GetMapping
    public List<UserQuestionnaire> getUserQuestionnaires() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userQuestionnaireService.getUserQuestionnaires(username);
    }

    /**
     * Retrieve a specific questionnaire by ID.
     */
    @GetMapping("/{questionnaireId}")
    public Optional<UserQuestionnaire> getQuestionnaireById(@PathVariable Long questionnaireId) {
        return userQuestionnaireService.getUserQuestionnaireSummary(questionnaireId);
    }
}


