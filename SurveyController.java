package com.example.controller;

import com.example.model.SurveyQuestion;
import com.example.model.SurveyResponse;
import com.example.model.SurveyQuestionOptions;
import com.example.service.SurveyService;
import com.example.repository.SurveyQuestionOptionsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/survey")
public class SurveyController {

    private static final Logger logger = Logger.getLogger(SurveyController.class.getName());

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyQuestionOptionsRepository surveyQuestionOptionsRepository;

    /**
     * ✅ Display the survey page with questions and answer choices
     */
    @GetMapping("/page")
    public String showSurveyPage(Model model) {
        logger.info("Rendering survey.html page...");
        List<SurveyQuestion> questions = surveyService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "survey";  // Ensure `survey.html` exists in templates
    }

    /**
     * ✅ Fetch all survey questions (for frontend JavaScript)
     */
    @GetMapping("/questions")
    @ResponseBody
    public List<SurveyQuestion> getSurveyQuestions() {
        logger.info("Fetching all survey questions...");
        List<SurveyQuestion> questions = surveyService.getAllQuestions();
        logger.info("Total questions retrieved: " + questions.size());
        return questions;
    }

    /**
     * ✅ Submit survey responses and analyze recommendations
     */
    @PostMapping("/submit")
    @ResponseBody
    public Map<String, Object> submitSurveyResponses(@RequestBody List<SurveyResponse> responses, Principal principal) {
        if (responses.isEmpty()) {
            return Map.of("error", "No survey responses provided.");
        }

        String username = (principal != null) ? principal.getName() : "Guest";
        logger.info("Received survey responses from user: " + username + ", count: " + responses.size());

        // Validate & ensure selected options exist
        for (SurveyResponse response : responses) {
            if (response.getSelectedOption() == null || response.getSelectedOption().getId() == null) {
                return Map.of("error", "Survey response is missing a selected option.");
            }

            Optional<SurveyQuestionOptions> selectedOption = surveyQuestionOptionsRepository.findById(response.getSelectedOption().getId());
            if (selectedOption.isEmpty()) {
                return Map.of("error", "Invalid option selected.");
            }
            response.setSelectedOption(selectedOption.get());
        }

        // Save responses
        surveyService.saveSurveyResponses(responses, username);
        logger.info("Survey responses saved successfully.");

        // Analyze responses to generate recommendations
        List<String> recommendedCategories = surveyService.analyzeResponses(username);
        logger.info("Generated recommendations for user: " + username + " -> " + recommendedCategories);

        return Map.of("recommendedCategories", recommendedCategories);
    }

    /**
     * ✅ Retrieve stored recommendation for the current user
     */
    @GetMapping("/recommendation")
    @ResponseBody
    public Map<String, Object> getUserRecommendation(Principal principal) {
        String username = (principal != null) ? principal.getName() : "Guest";
        logger.info("Fetching recommendations for user: " + username);

        List<String> recommendedCategories = surveyService.getUserRecommendation(username);
        logger.info("User " + username + " recommendations: " + recommendedCategories);

        return Map.of("recommendedCategories", recommendedCategories);
    }
}
