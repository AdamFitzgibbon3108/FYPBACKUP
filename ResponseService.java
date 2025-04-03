package com.example.service;

import com.example.model.Question;
import com.example.model.Response;
import com.example.model.User;
import com.example.repository.QuestionRepository;
import com.example.repository.ResponseRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Submits a response for a user and automatically assigns a score.
     */
    public Response submitResponse(Long questionId, Long userId, String selectedAnswer) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        String correctAnswer = question.getCorrectAnswer();
        boolean isCorrect = selectedAnswer.equalsIgnoreCase(correctAnswer);
        int score = isCorrect ? question.getScore() : 0;

        Response response = new Response();
        response.setQuestion(question);
        response.setUser(user);
        response.setSelectedAnswer(selectedAnswer);
        response.setCorrectAnswer(correctAnswer);
        response.setCorrect(isCorrect);
        response.setScore(score);
        response.setTimestamp(LocalDateTime.now());
        response.setRole(question.getRole());
        response.setCategory(question.getCategory());

        return responseRepository.save(response);
    }

    /**
     * Saves a response in the database.
     */
    public Response saveResponse(Response response) {
        return responseRepository.save(response);
    }

    /**
     * Retrieves all responses by a user.
     */
    public List<Response> getResponsesByUser(Long userId) {
        return responseRepository.findByUserId(userId);
    }

    /**
     * Retrieves all responses for a given question.
     */
    public List<Response> getResponsesByQuestion(Long questionId) {
        return responseRepository.findByQuestionId(questionId);
    }

    /**
     * Retrieves responses by a user's username.
     */
    public List<Response> getResponsesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return responseRepository.findByUserId(user.getId());
    }
}

