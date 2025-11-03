package com.example.Musify.service.impl;

import com.example.Musify.model.Song;
import com.example.Musify.repository.SongRepository;
import com.example.Musify.service.SongService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public Song uploadSong(MultipartFile file, String title, String artist) throws IOException {
        // Ensure upload folder exists
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // Create unique filename
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Save the file
        Files.copy(file.getInputStream(), filePath);

        // Create Song object
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setFilePath(filePath.toString());
        song.setUrl("/uploads/" + fileName);

        // Save to MongoDB
        return songRepository.save(song);
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }
}

