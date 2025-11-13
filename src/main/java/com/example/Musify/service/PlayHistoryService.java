package com.example.Musify.service;

import com.example.Musify.model.PlayHistory;
import com.example.Musify.model.Song;
import com.example.Musify.repository.PlayHistoryRepository;
import com.example.Musify.repository.SongRepository;
import com.example.Musify.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayHistoryService {

    private final PlayHistoryRepository playHistoryRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public PlayHistoryService(PlayHistoryRepository playHistoryRepository,
                              UserRepository userRepository,
                              SongRepository songRepository) {
        this.playHistoryRepository = playHistoryRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    /**
     * Record that a user played a song
     */
    public PlayHistory recordPlay(String userEmail, String songId) {
        // Get user ID from email
        String userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        // Verify song exists
        songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // Create and save play history record
        PlayHistory playHistory = new PlayHistory(userId, songId);
        return playHistoryRepository.save(playHistory);
    }

    /**
     * Get play history for a user with song details
     */
    public List<PlayHistoryWithSong> getPlayHistory(String userEmail) {
        String userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<PlayHistory> history = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);

        return history.stream()
                .map(ph -> {
                    Song song = songRepository.findById(ph.getSongId()).orElse(null);
                    return new PlayHistoryWithSong(ph, song);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get recent play history (last N plays)
     */
    public List<PlayHistoryWithSong> getRecentPlayHistory(String userEmail, int limit) {
        String userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<PlayHistory> history = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);

        return history.stream()
                .limit(limit)
                .map(ph -> {
                    Song song = songRepository.findById(ph.getSongId()).orElse(null);
                    return new PlayHistoryWithSong(ph, song);
                })
                .collect(Collectors.toList());
    }

    /**
     * Clear play history for a user
     */
    public void clearPlayHistory(String userEmail) {
        String userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<PlayHistory> history = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);
        playHistoryRepository.deleteAll(history);
    }

    /**
     * Get aggregated play history in format needed for recommendations
     * Returns: List of {song_id, plays} sorted by play count (descending)
     */
    public List<Map<String, Object>> getAggregatedPlayHistory(String userEmail) {
        String userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        List<PlayHistory> history = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);

        // Aggregate by song_id and count plays
        Map<String, Long> playCounts = history.stream()
                .collect(Collectors.groupingBy(
                        PlayHistory::getSongId,
                        Collectors.counting()
                ));

        // Convert to list of maps sorted by play count (descending)
        return playCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("song_id", entry.getKey());
                    item.put("plays", entry.getValue().intValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get aggregated play history by user ID (for recommendation system)
     */
    public List<Map<String, Object>> getAggregatedPlayHistoryByUserId(String userId) {
        List<PlayHistory> history = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);

        // Aggregate by song_id and count plays
        Map<String, Long> playCounts = history.stream()
                .collect(Collectors.groupingBy(
                        PlayHistory::getSongId,
                        Collectors.counting()
                ));

        // Convert to list of maps sorted by play count (descending)
        return playCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("song_id", entry.getKey());
                    item.put("plays", entry.getValue().intValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * DTO class to combine PlayHistory with Song details
     */
    public static class PlayHistoryWithSong {
        private PlayHistory playHistory;
        private Song song;

        public PlayHistoryWithSong(PlayHistory playHistory, Song song) {
            this.playHistory = playHistory;
            this.song = song;
        }

        public PlayHistory getPlayHistory() {
            return playHistory;
        }

        public void setPlayHistory(PlayHistory playHistory) {
            this.playHistory = playHistory;
        }

        public Song getSong() {
            return song;
        }

        public void setSong(Song song) {
            this.song = song;
        }
    }
}


