package com.example.controller;

import com.example.model.Question;
import com.example.model.Questionnaire;
import com.example.model.User;
import com.example.service.AdminService;
import com.example.service.AdminService.AdminDashboardStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;

    // ✅ Admin Dashboard UI
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        model.addAttribute("adminName", principal.getName());

        AdminDashboardStats stats = adminService.getAdminDashboardStats();
        model.addAttribute("totalUsers", stats.getTotalUsers());
        model.addAttribute("activeUsers", stats.getActiveUsers());
        model.addAttribute("pendingUsers", stats.getPendingApprovals());

        return "admin-dashboard";
    }

    // ✅ Manage Questions Page
    @GetMapping("/questions")
    public String manageQuestions(Model model) {
        model.addAttribute("questions", adminService.getAllQuestions());
        return "manage-questions"; // Ensure this file exists in src/main/resources/templates
    }

    // ✅ Manage Questionnaires Page (If Needed)
    @GetMapping("/questionnaires")
    public String manageQuestionnaires(Model model) {
        model.addAttribute("questionnaires", adminService.getAllQuestionnaires());
        return "manage-questionnaires"; // Ensure this file exists in templates
    }

    // ✅ Manage Users Page
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "manage-users"; // Ensure this file exists in templates
    }

    // ✅ API: Get all questions
    @GetMapping("/api/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(adminService.getAllQuestions());
    }

    // ✅ API: Get all questionnaires
    @GetMapping("/api/questionnaires")
    public ResponseEntity<List<Questionnaire>> getAllQuestionnaires() {
        return ResponseEntity.ok(adminService.getAllQuestionnaires());
    }

    // ✅ API: Get a specific questionnaire by ID
    @GetMapping("/api/questionnaires/{questionnaireId}")
    public ResponseEntity<Questionnaire> getQuestionnaireById(@PathVariable Long questionnaireId) {
        return ResponseEntity.ok(adminService.getQuestionnaireById(questionnaireId));
    }

    // ✅ API: Create a new questionnaire
    @PostMapping("/api/questionnaires")
    public ResponseEntity<Questionnaire> createQuestionnaire(@RequestBody Map<String, Object> requestData, Authentication authentication) {
        String title = (String) requestData.get("title");
        String description = (String) requestData.get("description");
        String adminUsername = authentication.getName();

        return ResponseEntity.ok(adminService.createQuestionnaire(title, description, adminUsername));
    }

    // ✅ API: Assign questions to a questionnaire
    @PostMapping("/api/questionnaires/{questionnaireId}/assign")
    public ResponseEntity<Questionnaire> assignQuestionsToQuestionnaire(
            @PathVariable Long questionnaireId,
            @RequestBody List<Long> questionIds) {
        return ResponseEntity.ok(adminService.assignQuestionsToQuestionnaire(questionnaireId, questionIds));
    }

    // ✅ API: Remove questions from a questionnaire
    @PostMapping("/api/questionnaires/{questionnaireId}/remove")
    public ResponseEntity<Questionnaire> removeQuestionsFromQuestionnaire(
            @PathVariable Long questionnaireId,
            @RequestBody List<Long> questionIds) {
        return ResponseEntity.ok(adminService.removeQuestionsFromQuestionnaire(questionnaireId, questionIds));
    }

    // ✅ API: Delete a questionnaire
    @DeleteMapping("/api/questionnaires/{questionnaireId}")
    public ResponseEntity<String> deleteQuestionnaire(@PathVariable Long questionnaireId) {
        adminService.deleteQuestionnaire(questionnaireId);
        return ResponseEntity.ok("Questionnaire deleted successfully.");
    }

    // ✅ API: Get all users
    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // ✅ API: Assign a role to a user
    @PostMapping("/api/users/{userId}/assign-role")
    public ResponseEntity<String> assignRoleToUser(@PathVariable Long userId, @RequestParam String role) {
        adminService.assignRoleToUser(userId, role);
        return ResponseEntity.ok("Role assigned successfully.");
    }

    // ✅ API: Remove a role from a user
    @PostMapping("/api/users/{userId}/remove-role")
    public ResponseEntity<String> removeRoleFromUser(@PathVariable Long userId, @RequestParam String role) {
        adminService.removeRoleFromUser(userId, role);
        return ResponseEntity.ok("Role removed successfully.");
    }

    // ✅ API: Delete a user
    @DeleteMapping("/api/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
