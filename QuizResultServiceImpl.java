package com.example.service;

import com.example.model.QuizResult;
import com.example.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizResultServiceImpl implements QuizResultService {

    private final QuizResultRepository quizResultRepository;

    @Autowired
    public QuizResultServiceImpl(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    @Override
    public List<QuizResult> findByUserId(Long userId) {
        return quizResultRepository.findByUserId(userId);
    }

    // New method to save a result
    @Override
    public QuizResult saveResult(QuizResult quizResult) {
        return quizResultRepository.save(quizResult);
    }

    // Optional helper method for calculating rounded percentage
    public int calculateRoundedPercentage(int totalScore, int totalPossibleScore) {
        if (totalPossibleScore == 0) return 0;
        return (int) Math.round(((double) totalScore / totalPossibleScore) * 100);
    }
}
