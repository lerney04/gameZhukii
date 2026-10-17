package com.example.gamezhukii;

public class RecordEntry {
    public final String playerName;
    public final int points;
    public final int difficulty;
    public final long playedAt;

    public RecordEntry(String playerName, int points, int difficulty, long playedAt) {
        this.playerName = playerName;
        this.points = points;
        this.difficulty = difficulty;
        this.playedAt = playedAt;
    }
}
