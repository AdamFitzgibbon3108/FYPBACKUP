package com.example.controller;

import com.example.model.*;
import com.example.repository.UserRepository;
import com.example.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private UserRepository userRepository;

    // API to get all survey questions
    @GetMapping
    public List<SurveyQuestion> getSurveyQuestions() {
        return surveyService.getAllQuestions();
    }

    // API to submit survey responses
    @PostMapping("/submit")
    public String submitSurveyResponses(@RequestBody List<SurveyResponse> responses, @RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Save responses and analyze recommendation
        surveyService.saveSurveyResponses(responses);
        String recommendedCategory = surveyService.analyzeResponses(user, responses);

        return "Survey submitted successfully. Recommended category: " + recommendedCategory;
    }
}

