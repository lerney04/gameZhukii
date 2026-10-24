package com.example.gamezhukii

import kotlin.math.cos
import kotlin.math.ceil
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
    var bonusAvailable = false
        private set
    var tiltEnabled = false
        private set
    var bonusX = 0.5f
        private set
    var bonusY = 0.5f
        private set
    private var bonusRemaining = 0f
    var tiltX = 0f
    var tiltY = 0f

    fun collectBonus(): Boolean {
        if (!bonusAvailable || isFinished) return false
        bonusAvailable = false
        tiltEnabled = true
        bonusRemaining = 5f
        return true
    }

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
        bonusAvailable = false
        tiltEnabled = false
        bonusRemaining = 0f
        tiltX = 0f
        tiltY = 0f
        nextId = 1L
        repeat(minOf(3, config.maxBugs)) { spawnRandomBug() }
    }

    fun update(deltaSeconds: Float) {
        if (isFinished || deltaSeconds <= 0f) return

        elapsedSeconds = (elapsedSeconds + deltaSeconds)
            .coerceAtMost(config.roundDurationSeconds.toFloat())
        val activeDelta = minOf(deltaSeconds, bonusRemaining)
        bugs.forEach {
            if (tiltEnabled) {
                it.x = (it.x + tiltX * activeDelta * 0.6f)
                    .coerceIn(it.type.radius, 1f - it.type.radius)
                it.y = (it.y + tiltY * activeDelta * 0.6f)
                    .coerceIn(it.type.radius, 1f - it.type.radius)
                if (deltaSeconds > activeDelta) {
                    it.move(deltaSeconds - activeDelta, config.speedMultiplier)
                }
            } else {
                it.move(deltaSeconds, config.speedMultiplier)
            }
        }
        bonusRemaining = (bonusRemaining - deltaSeconds).coerceAtLeast(0f)
        tiltEnabled = bonusRemaining > 0f && !isFinished
        bonusElapsed += deltaSeconds
        if (bonusElapsed >= config.bonusIntervalSeconds.coerceAtLeast(1) && !isFinished) {
            bonusElapsed %= config.bonusIntervalSeconds.coerceAtLeast(1).toFloat()
            bonusAvailable = true
            bonusX = 0.12f + random.nextFloat() * 0.76f
            bonusY = 0.12f + random.nextFloat() * 0.76f
        }

        spawnElapsed += deltaSeconds
        val spawnDelay = (1.4f / config.speedMultiplier).coerceAtLeast(0.35f)
        if (spawnElapsed >= spawnDelay && bugs.size < config.maxBugs) {
            spawnElapsed = 0f
            spawnRandomBug()
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
        remainingSeconds = ceil(
            (config.roundDurationSeconds - elapsedSeconds).coerceAtLeast(0f)
        ).toInt()
    )

    private fun spawnRandomBug() {
        val roll = random.nextFloat()
        val type = when {
            roll < RARE_BUG_CHANCE -> BugType.RARE
            roll < RARE_BUG_CHANCE + FAST_BUG_CHANCE -> BugType.FAST
            else -> BugType.NORMAL
        }
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
        const val FAST_BUG_CHANCE = 0.25f
        const val RARE_BUG_CHANCE = 0.1f
        const val FULL_CIRCLE = 6.2831855f
        const val MISS_PENALTY = 5
    }
}
