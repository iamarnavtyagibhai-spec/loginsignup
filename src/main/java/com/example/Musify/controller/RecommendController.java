package com.example.Musify.controller;

import com.example.Musify.service.MLRecommendService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin("*")
public class RecommendController {

    private final MLRecommendService mlService;

    public RecommendController(MLRecommendService mlService) {
        this.mlService = mlService;
    }

    // ✅ Save user listening event
    @PostMapping("/event")
    public Map<String, Object> saveUserEvent(@RequestBody Map<String, String> data) {
        String userId = data.get("user_id");
        String songId = data.get("song_id");
        return mlService.sendUserEvent(userId, songId);
    }

    // ✅ Get next recommended song
    @PostMapping("/next")
    public Map<String, Object> nextSong(@RequestBody Map<String, String> data) {
        String userId = data.get("user_id");
        String songId = data.get("id");
        return mlService.getNextSong(userId, songId);
    }

    // ✅ Get home recommended list
    @PostMapping("/home")
    public Map<String, Object> homeRecommend(@RequestBody Map<String, String> data) {
        String userId = data.get("user_id");
        return mlService.getHomeRecommendations(userId);
    }
}
