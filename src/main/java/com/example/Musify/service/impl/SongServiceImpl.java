package com.example.Musify.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
import java.util.Map;
import java.util.UUID;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final Cloudinary cloudinary;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public SongServiceImpl(SongRepository songRepository, Cloudinary cloudinary) {
        this.songRepository = songRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    public Song uploadSong(MultipartFile file, MultipartFile image, String title, String artist) throws IOException {

        // Upload audio to Cloudinary
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("resource_type", "video")
        );
        String audioUrl = (String) uploadResult.get("secure_url");

        // Ensure uploads directory exists
        Path path = Paths.get(uploadDir);
        Files.createDirectories(path);

        // Save image locally (correct path join)
        String imageFileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        File saveFile = new File(uploadDir, imageFileName); // ✅ FIXED

        image.transferTo(saveFile);

        String imagePath = "/images/" + imageFileName;

        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setAudioUrl(audioUrl);
        song.setImagePath(imagePath);

        return songRepository.save(song);
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Override
    public List<Song> searchSongs(String query) {
        return songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(query, query);
    }
}
