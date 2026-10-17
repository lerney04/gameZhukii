package com.example.gamezhukii;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "players",
        indices = {@Index(value = {"fullName"}, unique = true)}
)
public class PlayerEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public final String fullName;
    public final String gender;
    public final int course;
    public final int difficulty;
    public final String birthDate;
    public final String zodiacSign;

    public PlayerEntity(
            long id,
            String fullName,
            String gender,
            int course,
            int difficulty,
            String birthDate,
            String zodiacSign
    ) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.course = course;
        this.difficulty = difficulty;
        this.birthDate = birthDate;
        this.zodiacSign = zodiacSign;
    }
}
