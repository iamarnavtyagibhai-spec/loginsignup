package com.example.Musify.controller;

import com.example.Musify.model.User;
import com.example.Musify.repository.UserRepository;
import com.example.Musify.service.MLRecommendService;
import com.example.Musify.service.PlayHistoryService;
import com.example.Musify.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin("*")
public class RecommendController {

    private final MLRecommendService mlService;
    private final PlayHistoryService playHistoryService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public RecommendController(MLRecommendService mlService,
                               PlayHistoryService playHistoryService,
                               UserRepository userRepository,
                               JwtUtil jwtUtil) {
        this.mlService = mlService;
        this.playHistoryService = playHistoryService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Extract current user email from JWT token
     */
    private String getCurrentUserEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getSubject(token);
            }
        }
        return null;
    }

    /**
     * Get user ID from email
     */
    private String getUserIdFromEmail(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(null);
    }

    /**
     * Extract history from request body
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractHistoryFromRequest(Map<String, Object> data) {
        if (data != null && data.containsKey("history") && data.get("history") instanceof List) {
            List<Object> historyList = (List<Object>) data.get("history");
            List<Map<String, Object>> history = new ArrayList<>();
            for (Object item : historyList) {
                if (item instanceof Map) {
                    history.add((Map<String, Object>) item);
                }
            }
            return history;
        }
        return new ArrayList<>();
    }

    /**
     * Merge database history with request history
     * Request history takes precedence for play counts
     */
    private List<Map<String, Object>> mergeHistories(List<Map<String, Object>> dbHistory, 
                                                      List<Map<String, Object>> requestHistory) {
        Map<String, Integer> merged = new HashMap<>();
        
        // Add database history first
        for (Map<String, Object> item : dbHistory) {
            String songId = String.valueOf(item.get("song_id"));
            Integer plays = item.get("plays") instanceof Integer ? 
                (Integer) item.get("plays") : 
                ((Number) item.get("plays")).intValue();
            merged.put(songId, plays);
        }
        
        // Override with request history (request takes precedence)
        for (Map<String, Object> item : requestHistory) {
            String songId = String.valueOf(item.get("song_id"));
            Integer plays = item.get("plays") instanceof Integer ? 
                (Integer) item.get("plays") : 
                ((Number) item.get("plays")).intValue();
            merged.put(songId, merged.getOrDefault(songId, 0) + plays);
        }
        
        // Convert back to list format
        return merged.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("song_id", entry.getKey());
                    item.put("plays", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    // ✅ Save user listening event
    @PostMapping("/event")
    public Map<String, Object> saveUserEvent(@RequestBody Map<String, Object> data,
                                              HttpServletRequest request) {
        String userEmail = getCurrentUserEmail(request);
        String userId;
        String songId;
        
        // Try to get from JWT first, fallback to request body
        if (userEmail != null) {
            userId = getUserIdFromEmail(userEmail);
            if (userId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "User not found");
                return error;
            }
        } else if (data != null && data.containsKey("user_id")) {
            userId = String.valueOf(data.get("user_id"));
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User ID required");
            return error;
        }
        
        if (data != null && data.containsKey("song_id")) {
            songId = String.valueOf(data.get("song_id"));
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Song ID required");
            return error;
        }
        
        // Also record in play history if authenticated
        if (userEmail != null) {
            try {
                playHistoryService.recordPlay(userEmail, songId);
            } catch (Exception e) {
                // Log but don't fail the request
                System.err.println("Failed to record play history: " + e.getMessage());
            }
        }
        
        return mlService.sendUserEvent(userId, songId);
    }

    // ✅ Get next recommended song
    @PostMapping("/next")
    public Map<String, Object> nextSong(@RequestBody Map<String, Object> data,
                                         HttpServletRequest request) {
        String userEmail = getCurrentUserEmail(request);
        String userId;
        String songId;
        List<Map<String, Object>> history = new ArrayList<>();
        
        // Try to get from JWT first, fallback to request body
        if (userEmail != null) {
            userId = getUserIdFromEmail(userEmail);
            if (userId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "User not found");
                return error;
            }
            // Fetch play history from database
            try {
                List<Map<String, Object>> dbHistory = playHistoryService.getAggregatedPlayHistory(userEmail);
                List<Map<String, Object>> requestHistory = extractHistoryFromRequest(data);
                history = mergeHistories(dbHistory, requestHistory);
            } catch (Exception e) {
                System.err.println("Failed to fetch play history: " + e.getMessage());
                history = extractHistoryFromRequest(data);
            }
        } else if (data != null && data.containsKey("user_id")) {
            userId = String.valueOf(data.get("user_id"));
            history = extractHistoryFromRequest(data);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User ID required");
            return error;
        }
        
        if (data != null && data.containsKey("id")) {
            songId = String.valueOf(data.get("id"));
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Song ID (id) required");
            return error;
        }
        
        return mlService.getNextSong(userId, songId, history);
    }

    // ✅ Get home recommended list
    @PostMapping("/home")
    public Map<String, Object> homeRecommend(@RequestBody Map<String, Object> data,
                                              HttpServletRequest request) {
        String userEmail = getCurrentUserEmail(request);
        String userId;
        List<Map<String, Object>> history = new ArrayList<>();
        
        // Try to get from JWT first, fallback to request body
        if (userEmail != null) {
            userId = getUserIdFromEmail(userEmail);
            if (userId == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "User not found");
                return error;
            }
            // Fetch play history from database
            try {
                List<Map<String, Object>> dbHistory = playHistoryService.getAggregatedPlayHistory(userEmail);
                List<Map<String, Object>> requestHistory = extractHistoryFromRequest(data);
                history = mergeHistories(dbHistory, requestHistory);
            } catch (Exception e) {
                System.err.println("Failed to fetch play history: " + e.getMessage());
                history = extractHistoryFromRequest(data);
            }
        } else if (data != null && data.containsKey("user_id")) {
            userId = String.valueOf(data.get("user_id"));
            history = extractHistoryFromRequest(data);
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User ID required");
            return error;
        }
        
        return mlService.getHomeRecommendations(userId, history);
    }
}
