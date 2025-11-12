package com.example.Musify.service;

import com.example.Musify.model.Song;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface SongService {

    // ✅ Added category parameter
    Song uploadSong(
            MultipartFile file,
            MultipartFile image,
            String title,
            String artist,
            String category
    ) throws IOException;

    List<Song> getAllSongs();
    List<Song> searchSongs(String query);
}
