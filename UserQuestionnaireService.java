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
     * Creates a new User Questionnaire for a specific user by username.
     */
    public UserQuestionnaire createUserQuestionnaire(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        UserQuestionnaire userQuestionnaire = new UserQuestionnaire();
        userQuestionnaire.setUser(user);

        return userQuestionnaireRepository.save(userQuestionnaire);
    }

    /**
     * Retrieves all questionnaires for a given user by username.
     */
    public List<UserQuestionnaire> getUserQuestionnaires(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        return userQuestionnaireRepository.findByUserId(user.getId());
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

        if (questionnaire.getSelectedQuestions() == null) {
            questionnaire.setSelectedQuestions(new ArrayList<>());
        }

        if (questionnaire.getSelectedQuestions().contains(question)) {
            throw new IllegalArgumentException("Question already exists in the questionnaire.");
        }

        questionnaire.getSelectedQuestions().add(question);
        userQuestionnaireRepository.save(questionnaire);
    }

    /**
     * Removes a question from a user's questionnaire.
     */
    @Transactional
    public void removeQuestionFromUserQuestionnaire(Long questionnaireId, Long questionId) {
        UserQuestionnaire questionnaire = userQuestionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        if (questionnaire.getSelectedQuestions() != null) {
            questionnaire.getSelectedQuestions().remove(question);
            userQuestionnaireRepository.save(questionnaire);
        }
    }

    /**
     * Retrieves a full summary of a user questionnaire (including user details and selected questions).
     */
    public Optional<UserQuestionnaire> getUserQuestionnaireSummary(Long questionnaireId) {
        return userQuestionnaireRepository.findById(questionnaireId);
    }
}
