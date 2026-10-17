package com.example.gamezhukii;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertPlayer(PlayerEntity player);

    @Query("SELECT * FROM players WHERE fullName = :fullName LIMIT 1")
    PlayerEntity findPlayerByName(String fullName);

    @Query("SELECT * FROM players ORDER BY fullName COLLATE NOCASE")
    List<PlayerEntity> getPlayers();

    @Insert
    long insertScore(ScoreEntity score);

    @Query(
            "SELECT players.fullName AS playerName, scores.points, " +
            "scores.difficulty, scores.playedAt " +
            "FROM scores INNER JOIN players ON players.id = scores.playerId " +
            "ORDER BY scores.points DESC, scores.playedAt DESC"
    )
    List<RecordEntry> getRecords();
}
