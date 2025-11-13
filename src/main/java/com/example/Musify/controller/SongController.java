package com.example.Musify.controller;

import com.example.Musify.model.Song;
import com.example.Musify.service.PlayHistoryService;
import com.example.Musify.service.SongService;
import com.example.Musify.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin("*")
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private PlayHistoryService playHistoryService;

    @Autowired
    private JwtUtil jwtUtil;

    private String getCurrentUserEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getSubject(token);
            }
        }
        return null; // Return null if not authenticated (optional tracking)
    }

    // ✅ Upload Song (audio & image to Cloudinary) + Category
    @PostMapping("/upload")
    public ResponseEntity<Song> uploadSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam("image") MultipartFile image,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam("category") String category
    ) throws IOException {
        return ResponseEntity.ok(songService.uploadSong(file, image, title, artist, category));
    }

    // ✅ Get All Songs or Search
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(songService.searchSongs(search));
        }
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // ✅ Track when a song is played (optional authentication)
    @PostMapping("/{songId}/play")
    public ResponseEntity<Map<String, Object>> trackPlay(
            @PathVariable String songId,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        String userEmail = getCurrentUserEmail(request);
        if (userEmail != null) {
            try {
                playHistoryService.recordPlay(userEmail, songId);
                response.put("message", "Play tracked successfully");
            } catch (Exception e) {
                response.put("error", e.getMessage());
                return ResponseEntity.status(400).body(response);
            }
        } else {
            response.put("message", "Play tracking skipped (not authenticated)");
        }
        
        return ResponseEntity.ok(response);
    }
}
