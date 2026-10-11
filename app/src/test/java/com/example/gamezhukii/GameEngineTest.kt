package com.example.gamezhukii

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {
    private val config = GameConfig(
        speedMultiplier = 1f,
        maxBugs = 3,
        bonusIntervalSeconds = 2,
        roundDurationSeconds = 10
    )

    @Test
    fun bugCoordinatesStayInsideLogicalField() {
        val bug = GameBug(
            id = 1,
            type = BugType.NORMAL,
            x = 0.95f,
            y = 0.05f,
            velocityX = 1f,
            velocityY = -1f
        )

        bug.move(deltaSeconds = 1f, speedMultiplier = 1f)

        assertTrue(bug.x in bug.type.radius..(1f - bug.type.radius))
        assertTrue(bug.y in bug.type.radius..(1f - bug.type.radius))
        assertTrue(bug.velocityX < 0f)
        assertTrue(bug.velocityY > 0f)
    }

    @Test
    fun hitAddsPointsAndMissAppliesPenalty() {
        val engine = GameEngine(config, Random(1))
        engine.reset()
        engine.bugs.clear()
        engine.bugs += GameBug(1, BugType.FAST, 0.5f, 0.5f, 0f, 0f)

        assertEquals(BugType.FAST, engine.tap(0.5f, 0.5f))
        assertEquals(20, engine.score)
        assertEquals(null, engine.tap(0.9f, 0.9f))
        assertEquals(15, engine.score)
        assertEquals(1, engine.hits)
        assertEquals(1, engine.misses)
        assertEquals(50, engine.stats().accuracy)
    }

    @Test
    fun roundFinishesWhenTimerExpires() {
        val engine = GameEngine(config.copy(roundDurationSeconds = 1), Random(2))
        engine.reset()

        engine.update(0.25f)
        assertFalse(engine.isFinished)
        assertEquals(1, engine.stats().remainingSeconds)

        engine.update(0.75f)
        assertTrue(engine.isFinished)
        assertEquals(0, engine.stats().remainingSeconds)
    }

    @Test
    fun rareBugCanAppearDuringRegularSpawn() {
        val engine = GameEngine(
            config.copy(maxBugs = 1),
            object : Random() {
                override fun nextBits(bitCount: Int): Int = 0
            }
        )
        engine.reset()

        assertTrue(engine.bugs.any { it.type == BugType.RARE })
        assertEquals(1, engine.bugs.size)
    }
}
