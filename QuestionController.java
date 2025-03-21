package com.example.controller;

import com.example.model.Question;
import com.example.model.User;
import com.example.model.Response;
import com.example.model.QuizResult;
import com.example.service.QuestionService;
import com.example.repository.QuestionRepository;
import com.example.repository.ResponseRepository;
import com.example.repository.UserRepository;
import com.example.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizResultRepository quizResultRepository;
    
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);


    @GetMapping("/manage")
    public String manageQuestions(Model model) {
        model.addAttribute("questions", questionService.getAllQuestions());
        return "manage-questions";
    }

    @GetMapping("/roles")
    @ResponseBody
    public List<String> getAllRoles() {
        return questionService.getAllRoles();
    }

    @GetMapping("/questionnaire/custom")
    public String customQuestionnaire(Model model) {
        List<Question> allQuestions = questionService.getAllQuestions();
        model.addAttribute("questions", allQuestions);
        return "custom-questionnaire";
    }

    @GetMapping("/categories")
    @ResponseBody
    public List<String> getAllCategories() {
        return questionService.getAllCategories();
    }

    @GetMapping("/category/{category}")
    @ResponseBody
    public List<Question> getQuestionsByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @GetMapping("/start-role-only")
    public String startRoleBasedQuestionnaire(@RequestParam("selectedRole") String selectedRole, Model model) {
        List<Question> questions = questionService.getQuestionsByRole(selectedRole);
        model.addAttribute("questions", questions);
        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("selectedCategory", "None");
        return "questionnaire";
    }

    @GetMapping("/start")
    public String startQuestionnaire(@RequestParam("selectedRole") String selectedRole,
                                     @RequestParam(value = "selectedCategory", required = false) String selectedCategory,
                                     Model model) {
        List<Question> questions = questionService.getQuestionsByRoleAndCategory(selectedRole, selectedCategory);
        model.addAttribute("questions", questions);
        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("selectedCategory", selectedCategory);
        return "questionnaire";
    }

    @Transactional
    @PostMapping("/submit")
    public String submitQuestionnaire(@RequestParam Map<String, String> requestBody, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("📩 Received quiz submission from user: {}", username);

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.error("❌ User not found: {}", username);
            model.addAttribute("error", "User not found.");
            return "result";
        }
        User user = userOptional.get();
        log.debug("✅ Found user: {}", user.getUsername());

        String selectedRole = requestBody.get("selectedRole");
        String selectedCategory = requestBody.get("selectedCategory");
        log.info("📝 Quiz Role: {}, Category: {}", selectedRole, selectedCategory);

        if (selectedRole == null || selectedCategory == null) {
            log.error("❌ Missing role or category in submission data.");
            model.addAttribute("error", "Missing role or category.");
            return "result";
        }

        List<Response> savedResponses = new ArrayList<>();
        int totalScore = 0;

        log.debug("🔍 Extracting questions from request...");
        for (Map.Entry<String, String> entry : requestBody.entrySet()) {
            if (entry.getKey().startsWith("question_")) {
                try {
                    Long questionId = Long.parseLong(entry.getKey().replace("question_", ""));
                    String answer = entry.getValue();
                    log.info("📌 Processing Question ID: {}, Answer: {}", questionId, answer);

                    Optional<Question> questionOptional = questionRepository.findById(questionId);
                    if (questionOptional.isPresent()) {
                        Question question = questionOptional.get();
                        String correctAnswer = question.getCorrectAnswer();

                        int score = correctAnswer.trim().equalsIgnoreCase(answer.trim()) ? 1 : 0;
                        totalScore += score;
                        log.debug("✅ Question: {} | Correct Answer: {} | User Answer: {} | Score: {}",
                                question.getQuestionText(), correctAnswer, answer, score);

                        Response response = new Response();
                        response.setUser(user);
                        response.setQuestion(question);
                        response.setAnswer(answer);
                        response.setTimestamp(LocalDateTime.now());
                        response.setRole(selectedRole);
                        response.setCategory(selectedCategory);
                        response.setScore(score);

                        responseRepository.save(response);
                        savedResponses.add(response);
                    } else {
                        log.warn("⚠️ Question ID {} not found in database!", questionId);
                    }
                } catch (NumberFormatException e) {
                    log.error("❌ Error parsing question ID: {} | Exception: {}", entry.getKey(), e.getMessage());
                }
            }
        }

        if (!savedResponses.isEmpty()) {
            QuizResult quizResult = new QuizResult(user, totalScore, savedResponses.size(), selectedCategory, selectedRole);
            quizResultRepository.save(quizResult);
            log.info("🎯 Quiz result saved! User: {} | Score: {}/{}", user.getUsername(), totalScore, savedResponses.size());
        } else {
            log.error("❌ No valid responses recorded for user: {}", user.getUsername());
            model.addAttribute("error", "No responses recorded.");
            return "result";
        }

        // Add result details to the model
        model.addAttribute("score", totalScore);
        model.addAttribute("total", savedResponses.size());
        model.addAttribute("role", selectedRole);
        model.addAttribute("category", selectedCategory);

        log.info("✅ Submission completed! Redirecting to result page.");
        return "result"; // This renders result.html
    }
}