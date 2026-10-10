package com.example.gamezhukii

import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameEngine(
    private val config: GameConfig,
    private val random: Random = Random.Default
) {
    val bugs = mutableListOf<GameBug>()

    var score: Int = 0
        private set
    var hits: Int = 0
        private set
    var misses: Int = 0
        private set
    var elapsedSeconds: Float = 0f
        private set

    private var nextId = 1L
    private var spawnElapsed = 0f
    private var bonusElapsed = 0f

    val isFinished: Boolean
        get() = elapsedSeconds >= config.roundDurationSeconds

    fun reset() {
        bugs.clear()
        score = 0
        hits = 0
        misses = 0
        elapsedSeconds = 0f
        spawnElapsed = 0f
        bonusElapsed = 0f
        nextId = 1L
        repeat(minOf(3, config.maxBugs)) { spawnRegularBug() }
    }

    fun update(deltaSeconds: Float) {
        if (isFinished || deltaSeconds <= 0f) return

        elapsedSeconds = (elapsedSeconds + deltaSeconds)
            .coerceAtMost(config.roundDurationSeconds.toFloat())
        bugs.forEach { it.move(deltaSeconds, config.speedMultiplier) }

        spawnElapsed += deltaSeconds
        val spawnDelay = (1.4f / config.speedMultiplier).coerceAtLeast(0.35f)
        if (spawnElapsed >= spawnDelay && bugs.size < config.maxBugs) {
            spawnElapsed = 0f
            spawnRegularBug()
        }

        bonusElapsed += deltaSeconds
        if (bonusElapsed >= config.bonusIntervalSeconds && bugs.size < config.maxBugs) {
            bonusElapsed = 0f
            spawnBug(BugType.RARE)
        }
    }

    fun tap(logicalX: Float, logicalY: Float): BugType? {
        if (isFinished) return null
        val bug = bugs.asReversed().firstOrNull { it.contains(logicalX, logicalY) }
        return if (bug != null) {
            bugs.remove(bug)
            hits++
            score += bug.type.points
            bug.type
        } else {
            misses++
            score -= MISS_PENALTY
            null
        }
    }

    fun stats(): GameStats = GameStats(
        score = score,
        hits = hits,
        misses = misses,
        remainingSeconds = (config.roundDurationSeconds - elapsedSeconds)
            .coerceAtLeast(0f)
            .toInt()
    )

    private fun spawnRegularBug() {
        val type = if (random.nextFloat() < FAST_BUG_CHANCE) BugType.FAST else BugType.NORMAL
        spawnBug(type)
    }

    private fun spawnBug(type: BugType) {
        val radius = type.radius
        val angle = random.nextFloat() * FULL_CIRCLE
        bugs += GameBug(
            id = nextId++,
            type = type,
            x = radius + random.nextFloat() * (1f - radius * 2f),
            y = radius + random.nextFloat() * (1f - radius * 2f),
            velocityX = cos(angle),
            velocityY = sin(angle)
        )
    }

    private companion object {
        const val FAST_BUG_CHANCE = 0.3f
        const val FULL_CIRCLE = 6.2831855f
        const val MISS_PENALTY = 5
    }
}
