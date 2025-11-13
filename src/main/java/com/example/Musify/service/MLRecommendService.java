package com.example.Musify.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MLRecommendService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String USER_EVENT_URL = "https://group-task.onrender.com/ml/user_event";
    private static final String NEXT_SONG_URL = "https://group-task.onrender.com/recommend/next";
    private static final String HOME_RECOMMEND_URL = "https://group-task.onrender.com/recommend/home";

    // ✅ Send user listening event
    public Map<String, Object> sendUserEvent(String userId, String songId) {
        Map<String, String> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("song_id", songId);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                USER_EVENT_URL,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // ✅ Get next recommended song
    public Map<String, Object> getNextSong(String userId, String currentSongId, List<Map<String, Object>> history) {
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("id", currentSongId);
        body.put("history", history != null ? history : new java.util.ArrayList<>());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                NEXT_SONG_URL,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // ✅ Get home recommendations
    public Map<String, Object> getHomeRecommendations(String userId, List<Map<String, Object>> history) {
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("history", history != null ? history : new java.util.ArrayList<>());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                HOME_RECOMMEND_URL,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }
}
