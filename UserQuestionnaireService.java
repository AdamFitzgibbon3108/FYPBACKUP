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

import java.util.*;

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
     * @param userId ID of the user
     * @return The created UserQuestionnaire
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
     * Prevents duplicate questions.
     * @param questionnaireId ID of the questionnaire
     * @param questionId ID of the question to add
     */
    @Transactional
    public void addQuestionToUserQuestionnaire(Long questionnaireId, Long questionId) {
        UserQuestionnaire questionnaire = userQuestionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        // Ensure the list is initialized
        if (questionnaire.getSelectedQuestions() == null) {
            questionnaire.setSelectedQuestions(new ArrayList<>());
        }

        // Prevent duplicate additions
        if (questionnaire.getSelectedQuestions().contains(question)) {
            throw new IllegalArgumentException("Question already exists in the questionnaire.");
        }

        questionnaire.getSelectedQuestions().add(question);
        userQuestionnaireRepository.save(questionnaire);
    }

    /**
     * Removes a question from a user's questionnaire.
     * @param questionnaireId ID of the questionnaire
     * @param questionId ID of the question to remove
     */
    @Transactional
    public void removeQuestionFromUserQuestionnaire(Long questionnaireId, Long questionId) {
        UserQuestionnaire questionnaire = userQuestionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        if (questionnaire.getSelectedQuestions() != null && questionnaire.getSelectedQuestions().contains(question)) {
            questionnaire.getSelectedQuestions().remove(question);
            userQuestionnaireRepository.save(questionnaire);
        } else {
            throw new IllegalArgumentException("Question not found in the questionnaire.");
        }
    }

    /**
     * Retrieves all questionnaires for a given user.
     * @param userId ID of the user
     * @return List of UserQuestionnaires
     */
    public List<UserQuestionnaire> getUserQuestionnaires(Long userId) {
        return userQuestionnaireRepository.findByUserId(userId);
    }

    /**
     * Retrieves questions in a specific user questionnaire.
     * @param questionnaireId ID of the questionnaire
     * @return List of questions in the questionnaire
     */
    public List<Question> getQuestionsInUserQuestionnaire(Long questionnaireId) {
        UserQuestionnaire questionnaire = userQuestionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId));

        // Ensure a non-null list is returned
        return questionnaire.getSelectedQuestions() != null ? new ArrayList<Question>(questionnaire.getSelectedQuestions()) : new ArrayList<>();
    }



    /**
     * Retrieves a full summary of a user questionnaire (including user details and selected questions).
     * @param questionnaireId ID of the questionnaire
     * @return Optional UserQuestionnaire with details
     */
    public Optional<UserQuestionnaire> getUserQuestionnaireSummary(Long questionnaireId) {
        return userQuestionnaireRepository.findById(questionnaireId);
    }

    /**
     * Deletes an entire questionnaire by ID.
     * @param questionnaireId ID of the questionnaire to delete
     */
    @Transactional
    public void deleteUserQuestionnaire(Long questionnaireId) {
        if (!userQuestionnaireRepository.existsById(questionnaireId)) {
            throw new IllegalArgumentException("Questionnaire not found with ID: " + questionnaireId);
        }
        userQuestionnaireRepository.deleteById(questionnaireId);
    }
}
