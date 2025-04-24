package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.model.User;
import com.example.service.RegistrationService;

@Controller
@RequestMapping("/register")
public class RegistrationController {

	private final RegistrationService registrationService;
	private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	public RegistrationController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	// Display the registration page for users
	@GetMapping
	public String showRegistrationForm(Model model) {
		logger.info("🔍 Displaying registration form");
		model.addAttribute("user", new User());
		return "registration"; // Renders registration.html
	}

	// Handle user registration (Default Role: USER)
	@PostMapping
	public String registerUser(@ModelAttribute User user, Model model) {
		try {
			logger.info(" Attempting to register user: {}", user.getUsername());

			// === Password Validation (commented out for testing phase) ===
			// if (!isValidPassword(user.getPassword())) {
			// logger.warn("⚠️ Password does not meet security criteria");
			// model.addAttribute("error", "Password must be at least 8 characters, include
			// one uppercase letter, one number, and one special character.");
			// return "registration";
			// }

			registrationService.registerUser(user, "User"); // Assigns "USER" role by default

			logger.info("✅ User registered successfully: {}", user.getUsername());
			model.addAttribute("success", "User registered successfully!");
			return "redirect:/login";

		} catch (IllegalArgumentException e) {
			logger.error("🚨 Registration failed: {}", e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "registration";

		} catch (Exception e) {
			logger.error("❌ Unexpected error during registration", e);
			model.addAttribute("error", "An unexpected error occurred. Please try again.");
			return "registration";
		}
	}

	// ADMIN-ONLY: Register a new admin (Only an admin can access this endpoint)
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/admin")
	public String registerAdmin(@ModelAttribute User user, Model model) {
		try {
			logger.info("📝 Attempting to register admin: {}", user.getUsername());

			registrationService.registerAdmin(user); // Assigns "ADMIN" role

			logger.info("✅ Admin registered successfully: {}", user.getUsername());
			model.addAttribute("success", "Admin registered successfully!");
			return "redirect:/admin-dashboard";

		} catch (IllegalArgumentException e) {
			logger.error("🚨 Admin Registration failed: {}", e.getMessage());
			model.addAttribute("error", e.getMessage());
			return "admin-registration";

		} catch (Exception e) {
			logger.error("❌ Unexpected error during admin registration", e);
			model.addAttribute("error", "An unexpected error occurred. Please try again.");
			return "admin-registration";
		}
	}

	// Uncomment this method when ready to enforce secure passwords
	// private boolean isValidPassword(String password) {
	// return
	// password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
	// }
}
