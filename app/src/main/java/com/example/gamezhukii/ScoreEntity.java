package com.example.gamezhukii;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "scores",
        foreignKeys = @ForeignKey(
                entity = PlayerEntity.class,
                parentColumns = "id",
                childColumns = "playerId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"playerId"}, unique = true)}
)
public class ScoreEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public final long playerId;
    public final int points;
    public final int difficulty;
    public final long playedAt;

    public ScoreEntity(long id, long playerId, int points, int difficulty, long playedAt) {
        this.id = id;
        this.playerId = playerId;
        this.points = points;
        this.difficulty = difficulty;
        this.playedAt = playedAt;
    }
}
