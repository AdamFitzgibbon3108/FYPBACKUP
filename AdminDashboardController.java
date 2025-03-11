package com.example.controller;

import com.example.model.Question;
import com.example.model.Questionnaire;
import com.example.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller // ✅ Supports Thymeleaf
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // ✅ Restrict entire controller to ADMIN role
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;

    // ✅ Admin Dashboard UI
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        model.addAttribute("adminName", principal.getName()); // Display logged-in admin name
        return "admin-dashboard"; // ✅ Loads admin-dashboard.html
    }

    // ✅ API: Get all questions (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = adminService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    // ✅ API: Get all questionnaires (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/questionnaires")
    public ResponseEntity<List<Questionnaire>> getAllQuestionnaires() {
        List<Questionnaire> questionnaires = adminService.getAllQuestionnaires();
        return ResponseEntity.ok(questionnaires);
    }

    // ✅ API: Get a specific questionnaire by ID (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/questionnaires/{questionnaireId}")
    public ResponseEntity<Questionnaire> getQuestionnaireById(@PathVariable Long questionnaireId) {
        Questionnaire questionnaire = adminService.getQuestionnaireById(questionnaireId);
        return ResponseEntity.ok(questionnaire);
    }

    // ✅ API: Create a new questionnaire (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/questionnaires")
    public ResponseEntity<Questionnaire> createQuestionnaire(@RequestBody Map<String, Object> requestData) {
        String title = (String) requestData.get("title");
        String description = (String) requestData.get("description");
        Long adminId = ((Number) requestData.get("adminId")).longValue();

        Questionnaire createdQuestionnaire = adminService.createQuestionnaire(title, description, adminId);
        return ResponseEntity.ok(createdQuestionnaire);
    }

    // ✅ API: Assign questions to a questionnaire (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/questionnaires/{questionnaireId}/assign")
    public ResponseEntity<Questionnaire> assignQuestionsToQuestionnaire(
            @PathVariable Long questionnaireId,
            @RequestBody List<Long> questionIds) {

        Questionnaire updatedQuestionnaire = adminService.assignQuestionsToQuestionnaire(questionnaireId, questionIds);
        return ResponseEntity.ok(updatedQuestionnaire);
    }

    // ✅ API: Remove questions from a questionnaire (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/questionnaires/{questionnaireId}/remove")
    public ResponseEntity<Questionnaire> removeQuestionsFromQuestionnaire(
            @PathVariable Long questionnaireId,
            @RequestBody List<Long> questionIds) {

        Questionnaire updatedQuestionnaire = adminService.removeQuestionsFromQuestionnaire(questionnaireId, questionIds);
        return ResponseEntity.ok(updatedQuestionnaire);
    }

    // ✅ API: Delete a questionnaire (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/questionnaires/{questionnaireId}")
    public ResponseEntity<String> deleteQuestionnaire(@PathVariable Long questionnaireId) {
        adminService.deleteQuestionnaire(questionnaireId);
        return ResponseEntity.ok("Questionnaire deleted successfully.");
    }
}
