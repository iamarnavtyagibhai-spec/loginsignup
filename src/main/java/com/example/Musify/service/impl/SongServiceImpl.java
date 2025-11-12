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

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final Cloudinary cloudinary;

    public SongServiceImpl(SongRepository songRepository, Cloudinary cloudinary) {
        this.songRepository = songRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    public Song uploadSong(MultipartFile file, MultipartFile image, String title, String artist, String category) throws IOException {

        // ✅ Upload audio to Cloudinary
        Map<?, ?> audioUpload = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("resource_type", "video")
        );
        String audioUrl = (String) audioUpload.get("secure_url");

        // ✅ Upload image to Cloudinary
        Map<?, ?> imageUpload = cloudinary.uploader().upload(
                image.getBytes(),
                ObjectUtils.emptyMap()
        );
        String imageUrl = (String) imageUpload.get("secure_url");

        // ✅ Save to DB
        Song song = new Song();
        song.setTitle(title);
        song.setArtist(artist);
        song.setCategory(category); // ✅ NEW FIELD
        song.setAudioUrl(audioUrl);
        song.setImagePath(imageUrl);

        return songRepository.save(song);
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Override
    public List<Song> searchSongs(String query) {
        return songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                query, query, query
        );
    }
}
