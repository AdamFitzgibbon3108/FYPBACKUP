package com.example.service;

import dto.UserPerformanceDTO;
import com.example.model.QuizResult;
import com.example.model.User;
import com.example.repository.QuizResultRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPerformanceService {

    private final UserRepository userRepository;
    private final QuizResultRepository quizResultRepository;

    @Autowired
    public UserPerformanceService(UserRepository userRepository, QuizResultRepository quizResultRepository) {
        this.userRepository = userRepository;
        this.quizResultRepository = quizResultRepository;
    }

    public UserPerformanceDTO getUserPerformance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

        if (results.isEmpty()) {
            return new UserPerformanceDTO(user.getId(), user.getUsername(), 0, 0.0);
        }

        int totalQuizzes = results.size();
        double totalScore = results.stream().mapToDouble(QuizResult::getTotalScore).sum();
        double averageScore = totalScore / totalQuizzes;

        return new UserPerformanceDTO(user.getId(), user.getUsername(), totalQuizzes, averageScore);
    }
}

