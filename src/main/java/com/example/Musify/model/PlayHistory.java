package com.example.Musify.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "play_history")
public class PlayHistory {

    @Id
    private String id;

    @Indexed
    private String userId; // User ID who played the song

    @Indexed
    private String songId; // Song ID that was played

    private LocalDateTime playedAt; // Timestamp when the song was played

    public PlayHistory() {
        this.playedAt = LocalDateTime.now();
    }

    public PlayHistory(String userId, String songId) {
        this.userId = userId;
        this.songId = songId;
        this.playedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}


