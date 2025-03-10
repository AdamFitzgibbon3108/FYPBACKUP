package com.example.controller;

import com.example.model.Question;
import com.example.model.Questionnaire;
import com.example.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;

    // ✅ 1. Get all questions (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = adminService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    // ✅ 2. Get all questionnaires (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/questionnaires")
    public ResponseEntity<List<Questionnaire>> getAllQuestionnaires() {
        List<Questionnaire> questionnaires = adminService.getAllQuestionnaires();
        return ResponseEntity.ok(questionnaires);
    }

    // ✅ 3. Get a specific questionnaire by ID (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/questionnaires/{questionnaireId}")
    public ResponseEntity<Questionnaire> getQuestionnaireById(@PathVariable Long questionnaireId) {
        Questionnaire questionnaire = adminService.getQuestionnaireById(questionnaireId);
        return ResponseEntity.ok(questionnaire);
    }

    // ✅ 4. Create a new questionnaire (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questionnaires")
    public ResponseEntity<Questionnaire> createQuestionnaire(@RequestBody Map<String, Object> requestData) {
        String title = (String) requestData.get("title");
        String description = (String) requestData.get("description");
        Long adminId = ((Number) requestData.get("adminId")).longValue();

        Questionnaire createdQuestionnaire = adminService.createQuestionnaire(title, description, adminId);
        return ResponseEntity.ok(createdQuestionnaire);
    }

    // ✅ 5. Assign questions to a questionnaire (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questionnaires/{questionnaireId}/assign")
    public ResponseEntity<Questionnaire> assignQuestionsToQuestionnaire(
            @PathVariable Long questionnaireId,
            @RequestBody List<Long> questionIds) {

        Questionnaire updatedQuestionnaire = adminService.assignQuestionsToQuestionnaire(questionnaireId, questionIds);
        return ResponseEntity.ok(updatedQuestionnaire);
    }

    // ✅ 6. Remove questions from a questionnaire (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questionnaires/{questionnaireId}/remove")
    public ResponseEntity<Questionnaire> removeQuestionsFromQuestionnaire(
            @PathVariable Long questionnaireId,
            @RequestBody List<Long> questionIds) {

        Questionnaire updatedQuestionnaire = adminService.removeQuestionsFromQuestionnaire(questionnaireId, questionIds);
        return ResponseEntity.ok(updatedQuestionnaire);
    }

    // ✅ 7. Delete a questionnaire (Admin Only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/questionnaires/{questionnaireId}")
    public ResponseEntity<String> deleteQuestionnaire(@PathVariable Long questionnaireId) {
        adminService.deleteQuestionnaire(questionnaireId);
        return ResponseEntity.ok("Questionnaire deleted successfully.");
    }
}
