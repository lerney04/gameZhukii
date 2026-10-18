package com.example.gamezhukii;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {PlayerEntity.class, ScoreEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "DELETE FROM scores WHERE id NOT IN (" +
                    "SELECT (SELECT candidate.id FROM scores AS candidate " +
                    "WHERE candidate.playerId = source.playerId " +
                    "ORDER BY candidate.points DESC, candidate.playedAt DESC LIMIT 1) " +
                    "FROM scores AS source GROUP BY source.playerId)"
            );
            database.execSQL("DROP INDEX IF EXISTS index_scores_playerId");
            database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_scores_playerId ON scores(playerId)"
            );
        }
    };

    public abstract GameDao gameDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "bugs.db"
                    ).addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return instance;
    }
}
