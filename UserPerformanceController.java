package com.example.controller;

import dto.UserPerformanceDTO;
import com.example.service.UserPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPerformanceController {

    private final UserPerformanceService userPerformanceService;

    @Autowired
    public UserPerformanceController(UserPerformanceService userPerformanceService) {
        this.userPerformanceService = userPerformanceService;
    }

    @GetMapping("/user/performance")
    public String getUserPerformance(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Retrieves the currently logged-in user's username

        UserPerformanceDTO performance = userPerformanceService.getUserPerformance(username);

        model.addAttribute("performance", performance);
        return "user-performance";
    }
}

