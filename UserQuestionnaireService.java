package com.example.service;

import com.example.model.Question;
import com.example.model.User;
import com.example.model.UserQuestionnaire;
import com.example.repository.QuestionRepository;
import com.example.repository.UserQuestionnaireRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserQuestionnaireService {

    @Autowired
    private UserQuestionnaireRepository userQuestionnaireRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Creates a new User Questionnaire for a specific user.
     */
    public UserQuestionnaire createUserQuestionnaire(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        UserQuestionnaire userQuestionnaire = new UserQuestionnaire();
        userQuestionnaire.setUser(user);

        return userQuestionnaireRepository.save(userQuestionnaire);
    }

    /**
     * Adds a question to a user's questionnaire.
     */
    @Transactional
    public void addQuestionToUserQuestionnaire(Long questionnaireId, Long questionId) {
        UserQuestionnaire questionnaire = userQuestionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        // Ensure list is initialized before adding questions
        if (questionnaire.getSelectedQuestions() == null) {
            questionnaire.setSelectedQuestions(new ArrayList<>());
        }

        questionnaire.getSelectedQuestions().add(question);
        userQuestionnaireRepository.save(questionnaire);
    }

    /**
     * Retrieves all questionnaires for a given user.
     */
    public List<UserQuestionnaire> getUserQuestionnaires(Long userId) {
        return userQuestionnaireRepository.findByUserId(userId);
    }

    /**
     * Retrieves questions in a specific user questionnaire.
     */
    public List<Question> getQuestionsInUserQuestionnaire(Long questionnaireId) {
        Optional<UserQuestionnaire> questionnaire = userQuestionnaireRepository.findById(questionnaireId);
        return questionnaire.map(UserQuestionnaire::getSelectedQuestions).orElseThrow(() -> 
                new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId));
    }
}
