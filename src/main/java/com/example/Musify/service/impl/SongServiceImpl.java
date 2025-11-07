package com.example.Musify.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Musify.model.Song;
import com.example.Musify.repository.SongRepository;
import com.example.Musify.service.SongService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final Cloudinary cloudinary;

    public SongServiceImpl(SongRepository songRepository, Cloudinary cloudinary) {
        this.songRepository = songRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    public Song uploadSong(MultipartFile file, String title, String artist) throws IOException {
        // Create unique filename
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Upload file to Cloudinary
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "musify/songs",
                "public_id", fileName.replaceAll("\\.[^.]+$", ""), // Remove file extension
                "overwrite", false
        );

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        String publicId = (String) uploadResult.get("public_id");
        String secureUrl = (String) uploadResult.get("secure_url");

        // Create Song object
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setFilePath(publicId); // Store Cloudinary public ID
        song.setUrl(secureUrl); // Store Cloudinary URL

        // Save to MongoDB
        return songRepository.save(song);
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Override
    public List<Song> searchSongs(String query) {
        if (query == null || query.trim().isEmpty()) {
            return songRepository.findAll();
        }
        String trimmedQuery = query.trim();
        // Search in both title and artist fields (case-insensitive)
        List<Song> results = songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(trimmedQuery, trimmedQuery);
        // Return results (will be empty list if no matches found)
        return results;
    }
}

