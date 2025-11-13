package com.example.Musify.repository;

import com.example.Musify.model.PlayHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PlayHistoryRepository extends MongoRepository<PlayHistory, String> {

    // Find all play history for a user, ordered by most recent first
    List<PlayHistory> findByUserIdOrderByPlayedAtDesc(String userId);

    // Find play history for a specific user and song
    List<PlayHistory> findByUserIdAndSongIdOrderByPlayedAtDesc(String userId, String songId);

    // Count how many times a user played a specific song
    long countByUserIdAndSongId(String userId, String songId);

    // Find recent play history for a user (limit results)
    @Query(value = "{ 'userId': ?0 }", sort = "{ 'playedAt': -1 }")
    List<PlayHistory> findRecentByUserId(String userId);
}


