package com.example.Musify.controller;

import com.example.Musify.model.PlayHistory;
import com.example.Musify.service.PlayHistoryService;
import com.example.Musify.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@CrossOrigin("*")
public class PlayHistoryController {

    private final PlayHistoryService playHistoryService;
    private final JwtUtil jwtUtil;

    public PlayHistoryController(PlayHistoryService playHistoryService, JwtUtil jwtUtil) {
        this.playHistoryService = playHistoryService;
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
        throw new RuntimeException("Unauthorized");
    }

    /**
     * Record that a song was played
     * POST /api/history/play/{songId}
     */
    @PostMapping("/play/{songId}")
    public ResponseEntity<Map<String, Object>> recordPlay(
            @PathVariable String songId,
            HttpServletRequest request) {
        try {
            String userEmail = getCurrentUserEmail(request);
            PlayHistory playHistory = playHistoryService.recordPlay(userEmail, songId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Play recorded successfully");
            response.put("playHistory", playHistory);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    /**
     * Get detailed play history for the current user
     * GET /api/history
     */
    @GetMapping
    public ResponseEntity<List<PlayHistoryService.PlayHistoryWithSong>> getPlayHistory(
            HttpServletRequest request) {
        try {
            String userEmail = getCurrentUserEmail(request);
            List<PlayHistoryService.PlayHistoryWithSong> history = 
                    playHistoryService.getPlayHistory(userEmail);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Get recent play history (last N plays)
     * GET /api/history/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<List<PlayHistoryService.PlayHistoryWithSong>> getRecentPlayHistory(
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        try {
            String userEmail = getCurrentUserEmail(request);
            List<PlayHistoryService.PlayHistoryWithSong> history = 
                    playHistoryService.getRecentPlayHistory(userEmail, limit);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Get aggregated play history (song_id -> plays count)
     * This format is useful for recommendation systems
     * GET /api/history/aggregated
     */
    @GetMapping("/aggregated")
    public ResponseEntity<Map<String, Object>> getAggregatedPlayHistory(
            HttpServletRequest request) {
        try {
            String userEmail = getCurrentUserEmail(request);
            List<Map<String, Object>> history = 
                    playHistoryService.getAggregatedPlayHistory(userEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("history", history);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    /**
     * Clear all play history for the current user
     * DELETE /api/history
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearPlayHistory(
            HttpServletRequest request) {
        try {
            String userEmail = getCurrentUserEmail(request);
            playHistoryService.clearPlayHistory(userEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Play history cleared successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }
}

