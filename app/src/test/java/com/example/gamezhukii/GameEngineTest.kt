package com.example.gamezhukii

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {
    @Test
    fun bonusAppearsEveryFifteenSecondsAndResetsWithRound() {
        val engine = GameEngine(config.copy(roundDurationSeconds = 90))
        engine.reset()
        engine.update(14f)
        assertFalse(engine.bonusAvailable)
        engine.update(1f)
        assertTrue(engine.bonusAvailable)
        assertTrue(engine.collectBonus())
        assertTrue(engine.tiltEnabled)
        assertFalse(engine.bonusAvailable)
        engine.update(15f)
        assertTrue(engine.bonusAvailable)
        engine.reset()
        assertFalse(engine.bonusAvailable)
        assertFalse(engine.tiltEnabled)
    }

    @Test
    fun collectedBonusMovesBugsTowardTiltAndKeepsThemInsideField() {
        val engine = GameEngine(config.copy(roundDurationSeconds = 90))
        engine.reset()
        engine.update(15f)
        engine.collectBonus()
        val bug = engine.bugs.first()
        bug.x = 0.5f
        bug.y = 0.5f
        engine.tiltX = 1f
        engine.tiltY = 1f
        engine.update(0.1f)
        assertTrue(bug.x > 0.5f)
        assertTrue(bug.y > 0.5f)
        engine.update(10f)
        assertTrue(bug.x <= 1f - bug.type.radius)
        assertTrue(bug.y <= 1f - bug.type.radius)
    }
    @Test
    fun tiltBonusExpiresAfterFiveSeconds() {
        val engine = GameEngine(config.copy(roundDurationSeconds = 90), Random(42))
        engine.reset()
        engine.update(15f)
        assertTrue(engine.collectBonus())
        engine.update(4.9f)
        assertTrue(engine.tiltEnabled)
        engine.update(0.11f)
        assertFalse(engine.tiltEnabled)
        assertFalse(engine.collectBonus())
    }

    @Test
    fun bonusPositionChangesAtNextAppearanceAndStaysInsideField() {
        val engine = GameEngine(config.copy(roundDurationSeconds = 90), Random(42))
        engine.reset()
        engine.update(15f)
        val first = engine.bonusX to engine.bonusY
        assertTrue(engine.bonusX in 0.12f..0.88f)
        assertTrue(engine.bonusY in 0.12f..0.88f)
        engine.update(15f)
        assertTrue(first != (engine.bonusX to engine.bonusY))
        assertTrue(engine.bonusX in 0.12f..0.88f)
        assertTrue(engine.bonusY in 0.12f..0.88f)
    }

    @Test
    fun bonusUsesConfiguredInterval() {
        val engine = GameEngine(config.copy(bonusIntervalSeconds = 10, roundDurationSeconds = 90))
        engine.reset()
        engine.update(9f)
        assertFalse(engine.bonusAvailable)
        engine.update(1f)
        assertTrue(engine.bonusAvailable)
        engine.collectBonus()
        engine.update(9f)
        assertFalse(engine.bonusAvailable)
        engine.update(1f)
        assertTrue(engine.bonusAvailable)
    }

    private val config = GameConfig(
        speedMultiplier = 1f,
        maxBugs = 3,
        bonusIntervalSeconds = 15,
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
