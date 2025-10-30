package com.example.Musify.service;

import com.example.Musify.model.User;
import com.example.Musify.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Used by Google OAuth login
    public User upsert(String provider, String providerId, String email, String name) {
        return userRepository.findByEmail(email)
                .map(existing -> {
                    existing.setName(name);
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setEmail(email);
                    newUser.setProvider(provider);
                    newUser.setProviderId(providerId);
                    return userRepository.save(newUser);
                });
    }
}
