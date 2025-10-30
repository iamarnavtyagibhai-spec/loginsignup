package com.example.Musify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Musify.repository.UserRepository;
import com.example.Musify.model.User;

import java.util.Optional;

@Service
public class SignupService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User signup(User user) {

        Optional<User> existingByEmail = userRepository.findByEmail(user.getEmail());
        Optional<User> existingByUsername = userRepository.findByUsername(user.getUsername());



        if (existingByEmail.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (existingByUsername.isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save new user
        return userRepository.save(user);
    }
}
