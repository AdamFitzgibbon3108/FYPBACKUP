package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import com.example.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    // ✅ ADMIN-ONLY: Get all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ✅ Users can view their own profile, but only ADMIN can view any user
    @PreAuthorize("hasRole('ADMIN') or #principal.name == authentication.name")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id, Principal principal) {
        return userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // ✅ PUBLIC: Register a normal user (User Role Assigned)
    @PostMapping("/register")
    public RedirectView registerUser(@RequestBody User user) {
        return registrationService.registerUser(user, "User");
    }

    // ✅ ADMIN-ONLY: Register a new admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public RedirectView registerAdmin(@RequestBody User user) {
        return registrationService.registerAdmin(user);
    }

    // ✅ ADMIN-ONLY: Update user details
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    // ✅ ADMIN-ONLY: Delete a user
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
