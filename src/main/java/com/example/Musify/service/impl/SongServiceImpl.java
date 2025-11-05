package com.example.Musify.service.impl;

import com.example.Musify.model.Song;
import com.example.Musify.repository.SongRepository;
import com.example.Musify.service.SongService;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final GridFsTemplate gridFsTemplate;

    public SongServiceImpl(SongRepository songRepository, GridFsTemplate gridFsTemplate) {
        this.songRepository = songRepository;
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public Song uploadSong(MultipartFile file, String title, String artist) throws IOException {
        // Create unique filename
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Store file in MongoDB GridFS
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                fileName,
                file.getContentType()
        );

        // Create Song object
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setFilePath(fileId.toString()); // Store GridFS file ID
        song.setUrl("/api/songs/file/" + fileId.toString()); // API endpoint to retrieve file

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

