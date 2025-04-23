package com.example.service;

import dto.UserPerformanceDTO;
import dto.UserQuizHistoryDTO;
import com.example.model.QuizResult;
import com.example.model.User;
import com.example.model.SecurityControl;
import com.example.repository.QuizResultRepository;
import com.example.repository.UserRepository;
import com.example.repository.SecurityControlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPerformanceService {

    private final UserRepository userRepository;
    private final QuizResultRepository quizResultRepository;
    private final SecurityControlRepository securityControlRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserPerformanceService.class);

    @Autowired
    public UserPerformanceService(UserRepository userRepository, QuizResultRepository quizResultRepository, SecurityControlRepository securityControlRepository) {
        this.userRepository = userRepository;
        this.quizResultRepository = quizResultRepository;
        this.securityControlRepository = securityControlRepository;
    }

    public UserPerformanceDTO getUserPerformance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

        if (results.isEmpty()) {
            return new UserPerformanceDTO(user.getId(), user.getUsername(), 0, 0.0, 0, 0, "N/A", "N/A");
        }

        int totalQuizzes = results.size();
        double totalScore = results.stream().mapToDouble(QuizResult::getTotalScore).sum();
        double averageScore = totalScore / totalQuizzes;

        int bestScore = results.stream()
                .mapToInt(QuizResult::getTotalScore)
                .max()
                .orElse(0);

        int latestScore = results.stream()
                .max(Comparator.comparing(QuizResult::getCompletedAt))
                .map(QuizResult::getTotalScore)
                .orElse(0);

        String topCategory = results.stream()
                .collect(Collectors.groupingBy(QuizResult::getCategory, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        String mostUsedRole = results.stream()
                .collect(Collectors.groupingBy(QuizResult::getRole, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        return new UserPerformanceDTO(
                user.getId(),
                user.getUsername(),
                totalQuizzes,
                averageScore,
                bestScore,
                latestScore,
                topCategory,
                mostUsedRole
        );
    }

    public List<UserQuizHistoryDTO> getUserQuizHistory(String username) {
        List<QuizResult> results = quizResultRepository.findByUserUsername(username);
        return results.stream().map(result -> new UserQuizHistoryDTO(
                result.getCategory(),
                result.getRole(),
                result.getTotalScore(),
                result.getTotalQuestions(),
                result.getCompletedAt()
        )).collect(Collectors.toList());
    }

    public Map<String, Long> getScoreBucketDistribution(String username) {
        List<QuizResult> results = quizResultRepository.findByUserUsername(username);
        return results.stream()
                .map(r -> {
                    int percentage = (r.getTotalQuestions() == 0) ? 0 : (int) Math.round((r.getTotalScore() * 100.0) / r.getTotalQuestions());
                    int bucket = (percentage / 10) * 10;
                    return bucket >= 100 ? "100" : bucket + "–" + (bucket + 9);
                })
                .collect(Collectors.groupingBy(bucket -> bucket, TreeMap::new, Collectors.counting()));
    }

    public Map<String, Long> getPassFailDistribution(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

        return results.stream()
                .map(result -> Boolean.TRUE.equals(result.getPassed()) ? "Pass" : "Fail")
                .collect(Collectors.groupingBy(label -> label, TreeMap::new, Collectors.counting()));
    }

    public Map<String, Long> getCategoryGroupDistribution(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<QuizResult> results = quizResultRepository.findByUserId(user.getId());

        Map<String, Long> groupCounts = new TreeMap<>();
        for (QuizResult result : results) {
            String category = result.getCategory();
            SecurityControl control = securityControlRepository.findByNameIgnoreCase(category).orElse(null);
            String group = (control != null && control.getCategoryGroup() != null) ? control.getCategoryGroup() : "Other";
            groupCounts.put(group, groupCounts.getOrDefault(group, 0L) + 1);
        }

        return groupCounts;
    }
}
