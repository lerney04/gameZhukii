package com.example.gamezhukii

data class GameConfig(
    val speedMultiplier: Float,
    val maxBugs: Int,
    val bonusIntervalSeconds: Int,
    val roundDurationSeconds: Int
)

data class GameStats(
    val score: Int,
    val hits: Int,
    val misses: Int,
    val remainingSeconds: Int
) {
    val accuracy: Int
        get() {
            val attempts = hits + misses
            return if (attempts == 0) 0 else hits * 100 / attempts
        }
}
