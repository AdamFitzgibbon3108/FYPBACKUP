package com.example.controller;

import com.example.model.QuizResult;
import com.example.model.Response;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.repository.QuizResultRepository;
import com.example.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/responses")
public class ResponseController {

    private final ResponseService responseService;
    private final UserRepository userRepository;
    private final QuizResultRepository quizResultRepository;

    @Autowired
    public ResponseController(ResponseService responseService,
                              UserRepository userRepository,
                              QuizResultRepository quizResultRepository) {
        this.responseService = responseService;
        this.userRepository = userRepository;
        this.quizResultRepository = quizResultRepository;
    }

    @PostMapping("/submit")
    public String submitResponses(@RequestBody List<Response> responses, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Save each response
        responses.forEach(response -> {
            response.setUser(user);
            responseService.saveResponse(response);
        });

        int totalQuestions = responses.size();
        int totalScore = (int) responses.stream()
                .filter(response -> response.getSelectedAnswer().equalsIgnoreCase(response.getCorrectAnswer()))
                .count();

        double rawPercentage = ((double) totalScore / totalQuestions) * 100.0;
        int percentageScore = (int) Math.round(rawPercentage); // Rounded

        boolean passed = percentageScore >= 80;

        String category = responses.get(0).getCategory(); // assuming uniform category
        String role = responses.get(0).getRole();         // assuming uniform role
        String recommendation = getCategoryRecommendation(category);

        // Save quiz result
        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setTotalScore(totalScore);
        result.setTotalQuestions(totalQuestions);
        result.setCategory(category);
        result.setRole(role);
        result.setCompletedAt(LocalDateTime.now());
        result.setScorePercentage(percentageScore);
        result.setPassed(passed);
        result.setRecommendations(recommendation);

        quizResultRepository.save(result);

        // Flash attributes for the result page
        redirectAttributes.addFlashAttribute("username", username);
        redirectAttributes.addFlashAttribute("score", totalScore);
        redirectAttributes.addFlashAttribute("percentageScore", percentageScore);
        redirectAttributes.addFlashAttribute("passed", passed);
        redirectAttributes.addFlashAttribute("category", category);
        redirectAttributes.addFlashAttribute("role", role);
        redirectAttributes.addFlashAttribute("recommendation", recommendation);

        return "redirect:/result";
    }

    private String getCategoryRecommendation(String category) {
        return switch (category) {
            case "Secure Development" -> "Continue enhancing your secure coding skills with OWASP resources.";
            case "Web Security" -> "Review OWASP Top 10 and practice mitigating XSS and CSRF threats.";
            case "Database Security" -> "Focus on encryption, access control, and SQL injection prevention.";
            case "Security Governance" -> "Understand frameworks like ISO 27001 and NIST RMF.";
            case "Authentication" -> "Strengthen knowledge in MFA, SSO, and secure session handling.";
            case "Security Awareness" -> "Revisit phishing defense strategies and social engineering risks.";
            case "Network Security" -> "Brush up on firewall configuration, IDS/IPS systems, and VPN best practices.";
            case "Incident Response" -> "Learn from NIST’s incident response lifecycle and improve response planning.";
            case "Risk Management" -> "Improve risk analysis and alignment with business objectives.";
            case "Penetration Testing" -> "Explore tools like Burp Suite and methodologies like PTES.";
            case "Security Monitoring" -> "Study SIEM systems and anomaly detection techniques.";
            case "System Security" -> "Practice securing endpoints, patching, and OS hardening.";
            default -> "Keep building foundational knowledge across all security domains.";
        };
    }
}


