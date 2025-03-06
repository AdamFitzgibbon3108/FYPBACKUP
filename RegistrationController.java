package com.example.controller;

import com.example.model.User;
import com.example.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // Display the registration page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("🔍 Displaying registration form");
        model.addAttribute("user", new User());
        return "registration"; // Render registration.html
    }

    // Handle form submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            logger.info("📝 Attempting to register user: {}", user.getUsername());
            
            registrationService.registerUser(user);
            
            logger.info("✅ User registered successfully: {}", user.getUsername());
            model.addAttribute("success", "User registered successfully!");
            
            return "redirect:/login"; // Redirect to login page after successful registration
            
        } catch (IllegalArgumentException e) {
            logger.error("🚨 Registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "registration"; // Stay on the registration page and show error
            
        } catch (Exception e) {
            logger.error("❌ Unexpected error during registration", e);
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "registration";
        }
    }
}
