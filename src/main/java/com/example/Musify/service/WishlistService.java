package com.example.Musify.service;

import com.example.Musify.model.Song;
import com.example.Musify.model.User;
import com.example.Musify.repository.SongRepository;
import com.example.Musify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public WishlistService(UserRepository userRepository, SongRepository songRepository) {
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    public List<Song> getWishlist(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getWishlist() == null || user.getWishlist().isEmpty()) {
            return new ArrayList<>();
        }
        
        return user.getWishlist().stream()
                .map(songId -> songRepository.findById(songId))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
    }

    public String addToWishlist(String userEmail, String songId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        
        if (user.getWishlist() == null) {
            user.setWishlist(new ArrayList<>());
        }
        
        if (!user.getWishlist().contains(songId)) {
            user.getWishlist().add(songId);
            userRepository.save(user);
            return "Song added to wishlist";
        }
        
        return "Song already in wishlist";
    }

    public String removeFromWishlist(String userEmail, String songId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getWishlist() == null) {
            return "Wishlist is empty";
        }
        
        if (user.getWishlist().remove(songId)) {
            userRepository.save(user);
            return "Song removed from wishlist";
        }
        
        return "Song not found in wishlist";
    }

    public boolean isInWishlist(String userEmail, String songId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getWishlist() != null && user.getWishlist().contains(songId);
    }
}

