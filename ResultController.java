package com.example.controller;

import com.example.model.QuizResult;
import com.example.model.Response;
import com.example.service.QuizResultService;
import com.example.service.ResponseService;
import com.example.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/result")
public class ResultController {

    private final ResponseService responseService;
    private final QuizResultService quizResultService;
    private final UserService userService;

    @Autowired
    public ResultController(ResponseService responseService,
                            QuizResultService quizResultService,
                            UserService userService) {
        this.responseService = responseService;
        this.quizResultService = quizResultService;
        this.userService = userService;
    }

    @GetMapping
    public String showResultPage(Model model) {
        // Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).orElseThrow().getId();

        // Fetch all responses for user (optional, for breakdown)
        List<Response> responses = responseService.getResponsesByUsername(username);

        // Fetch latest quiz result
        List<QuizResult> results = quizResultService.findByUserId(userId);
        QuizResult latestResult = results.stream()
                .max(Comparator.comparing(QuizResult::getCompletedAt))
                .orElse(null);

        if (latestResult != null) {
            double percentage = latestResult.getScorePercentage();
            boolean passed = latestResult.isPassed();
            String recommendations = latestResult.getRecommendations();

            model.addAttribute("score", latestResult.getTotalScore());
            model.addAttribute("totalQuestions", latestResult.getTotalQuestions());
            model.addAttribute("percentage", String.format("%.2f", percentage));
            model.addAttribute("passed", passed);
            model.addAttribute("recommendations", recommendations);
            model.addAttribute("category", latestResult.getCategory());
            model.addAttribute("role", latestResult.getRole());
        } else {
            model.addAttribute("score", "N/A");
            model.addAttribute("totalQuestions", "N/A");
            model.addAttribute("percentage", "N/A");
            model.addAttribute("passed", false);
            model.addAttribute("recommendations", "No data available.");
            model.addAttribute("category", "N/A");
            model.addAttribute("role", "N/A");
        }

        model.addAttribute("username", username);
        model.addAttribute("responses", responses);

        return "result";
    }
}

