package com.example.Musify.controller;

import com.example.Musify.model.Song;
import com.example.Musify.service.WishlistService;
import com.example.Musify.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin("*")
public class WishlistController {

    private final WishlistService wishlistService;
    private final JwtUtil jwtUtil;

    public WishlistController(WishlistService wishlistService, JwtUtil jwtUtil) {
        this.wishlistService = wishlistService;
        this.jwtUtil = jwtUtil;
    }

    private String getCurrentUserEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getSubject(token);
            }
        }
        throw new RuntimeException("Unauthorized");
    }

    @GetMapping
    public ResponseEntity<List<Song>> getWishlist(HttpServletRequest request) {
        String userEmail = getCurrentUserEmail(request);
        return ResponseEntity.ok(wishlistService.getWishlist(userEmail));
    }

    @PostMapping(value = "/add/{songId}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> addToWishlist(
            @PathVariable String songId,
            HttpServletRequest request) {
        String userEmail = getCurrentUserEmail(request);
        String message = wishlistService.addToWishlist(userEmail, songId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{songId}")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(
            @PathVariable String songId,
            HttpServletRequest request) {
        String userEmail = getCurrentUserEmail(request);
        String message = wishlistService.removeFromWishlist(userEmail, songId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{songId}")
    public ResponseEntity<Map<String, Object>> checkInWishlist(
            @PathVariable String songId,
            HttpServletRequest request) {
        String userEmail = getCurrentUserEmail(request);
        boolean isInWishlist = wishlistService.isInWishlist(userEmail, songId);
        Map<String, Object> response = new HashMap<>();
        response.put("isInWishlist", isInWishlist);
        return ResponseEntity.ok(response);
    }
}

