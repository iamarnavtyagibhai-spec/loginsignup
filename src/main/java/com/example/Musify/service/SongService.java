package com.example.Musify.service;

import com.example.Musify.model.Song;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface SongService {
    Song uploadSong(MultipartFile file, String title, String artist) throws IOException;
    List<Song> getAllSongs();
    List<Song> searchSongs(String query);
}
