package com.example.service;

import com.example.model.Question;
import com.example.model.Questionnaire;
import com.example.model.User;
import com.example.repository.QuestionRepository;
import com.example.repository.QuestionnaireRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ 1. Get all questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // ✅ 2. Get all questionnaires
    public List<Questionnaire> getAllQuestionnaires() {
        return questionnaireRepository.findAll();
    }

    // ✅ 3. Create a new questionnaire and assign admin creator
    @Transactional
    public Questionnaire createQuestionnaire(String title, String description, Long adminId) {
        Optional<User> adminUser = userRepository.findById(adminId);
        if (adminUser.isPresent() && "ADMIN".equals(adminUser.get().getRole())) {
            Questionnaire questionnaire = new Questionnaire(title, description, adminUser.get(), null);
            return questionnaireRepository.save(questionnaire);
        } else {
            throw new RuntimeException("Admin user not found or invalid role.");
        }
    }

    // ✅ 4. Assign questions to a questionnaire
    @Transactional
    public Questionnaire assignQuestionsToQuestionnaire(Long questionnaireId, List<Long> questionIds) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("Questionnaire not found with ID: " + questionnaireId));

        List<Question> selectedQuestions = questionRepository.findAllById(questionIds);
        questionnaire.getQuestions().addAll(selectedQuestions);
        return questionnaireRepository.save(questionnaire);
    }

    // ✅ 5. Remove questions from a questionnaire
    @Transactional
    public Questionnaire removeQuestionsFromQuestionnaire(Long questionnaireId, List<Long> questionIds) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("Questionnaire not found with ID: " + questionnaireId));

        questionnaire.getQuestions().removeIf(q -> questionIds.contains(q.getId()));
        return questionnaireRepository.save(questionnaire);
    }

    // ✅ 6. Delete a questionnaire
    @Transactional
    public void deleteQuestionnaire(Long questionnaireId) {
        if (questionnaireRepository.existsById(questionnaireId)) {
            questionnaireRepository.deleteById(questionnaireId);
        } else {
            throw new RuntimeException("Questionnaire not found with ID: " + questionnaireId);
        }
    }

    // ✅ 7. Get a specific questionnaire by ID
    public Questionnaire getQuestionnaireById(Long questionnaireId) {
        return questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("Questionnaire not found with ID: " + questionnaireId));
    }
}


