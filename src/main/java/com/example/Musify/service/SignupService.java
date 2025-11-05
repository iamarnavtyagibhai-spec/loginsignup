package com.example.Musify.service;

import com.example.Musify.model.User;
import com.example.Musify.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(User user) {
        if (user == null) {
            throw new RuntimeException("User body is required");
        }

        String email = user.getEmail() == null ? null : user.getEmail().trim().toLowerCase();
        String username = user.getUsername() == null ? null : user.getUsername().trim();
        String password = user.getPassword();

        if ((email == null || email.isBlank()) || (username == null || username.isBlank())) {
            throw new RuntimeException("Email and username are required");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password is required");
        }

        Optional<User> existingByEmail = userRepository.findByEmail(email);
        Optional<User> existingByUsername = userRepository.findByUsername(username);

        if (existingByEmail.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (existingByUsername.isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }
}
