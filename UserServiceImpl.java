package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<User> getAllUsers() {
        System.out.println("📋 Fetching all users...");
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User createUser(User user) {
        // Convert username to lowercase for uniformity
        String normalizedUsername = user.getUsername().toLowerCase();
        System.out.println("🔍 Checking if username exists: " + normalizedUsername);

        // Check if username already exists
        Optional<User> existingUser = userRepository.findByUsername(normalizedUsername);
        if (existingUser.isPresent()) {
            System.err.println("🚨 User registration blocked: Username already exists -> " + normalizedUsername);
            throw new RuntimeException("User already exists with username: " + normalizedUsername);
        }

        System.out.println("✅ No existing user found, proceeding to save new user: " + normalizedUsername);

        // Debugging: Check if password is being received properly
        System.out.println("🔑 Raw password before saving: " + user.getPassword());

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("🔐 Hashed password before saving: " + hashedPassword);

        user.setUsername(normalizedUsername);
        user.setPassword(hashedPassword);

        // Save user and force commit
        User savedUser = userRepository.save(user);
        userRepository.flush();  // Ensures transaction commits

        System.out.println("✅ User successfully saved with ID: " + savedUser.getId());

        return savedUser;
    }

    @Override
    public User updateUser(Long id, User user) {
        System.out.println("🔄 Updating user with ID: " + id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword())); // Ensuring password remains hashed
        existingUser.setRoles(user.getRoles());

        User updatedUser = userRepository.save(existingUser);
        System.out.println("✅ User successfully updated with ID: " + updatedUser.getId());
        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        System.out.println("🗑 Deleting user with ID: " + id);
        userRepository.deleteById(id);
        System.out.println("✅ User deleted successfully.");
    }

    @Override
    public Optional<User> findByUsername(String username) {
        System.out.println("🔍 Looking for user: " + username);
        return userRepository.findByUsername(username.toLowerCase());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        System.out.println("🔍 Fetching user by ID: " + id);
        return userRepository.findById(id);
    }

    @Override
    public void markSurveyAsCompleted(Long userId, String recommendedCategory) {
        System.out.println("📝 Marking survey as completed for user ID: " + userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setSurveyCompleted(true);
            user.setRecommendedSecurityCategory(recommendedCategory);
            userRepository.save(user);
            System.out.println("✅ Survey marked as completed for user ID: " + userId);
        } else {
            System.err.println("❌ User not found for survey completion: " + userId);
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
