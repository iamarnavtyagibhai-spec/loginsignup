package com.example.Musify.repository;

import com.example.Musify.model.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SongRepository extends MongoRepository<Song, String> {

    List<Song> findByTitleContainingIgnoreCase(String title);

    List<Song> findByArtistContainingIgnoreCase(String artist);

    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(String title, String artist);

    // ✅ NEW: Find songs by category
    List<Song> findByCategoryIgnoreCase(String category);

    // ✅ NEW: Search by title/artist/category together
    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String title, String artist, String category
    );
}
