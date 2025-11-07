package com.example.Musify.controller;

import com.example.Musify.model.Song;
import com.example.Musify.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin("*")
public class SongController {

    @Autowired
    private SongService songService;

    @PostMapping("/upload")
    public ResponseEntity<Song> uploadSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist
    ) throws IOException {
        return ResponseEntity.ok(songService.uploadSong(file, title, artist));
    }

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(songService.searchSongs(search));
        }
        return ResponseEntity.ok(songService.getAllSongs());
    }
}
