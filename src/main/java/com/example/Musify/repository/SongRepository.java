package com.example.Musify.repository;

import com.example.Musify.model.Song;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SongRepository extends MongoRepository<Song, String> {
}
