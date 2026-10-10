package com.example.gamezhukii

import kotlin.math.sqrt

data class GameBug(
    val id: Long,
    val type: BugType,
    var x: Float,
    var y: Float,
    var velocityX: Float,
    var velocityY: Float
) {
    fun move(deltaSeconds: Float, speedMultiplier: Float) {
        x += velocityX * type.speed * speedMultiplier * deltaSeconds
        y += velocityY * type.speed * speedMultiplier * deltaSeconds

        if (x < type.radius) {
            x = type.radius
            velocityX = -velocityX
        } else if (x > 1f - type.radius) {
            x = 1f - type.radius
            velocityX = -velocityX
        }

        if (y < type.radius) {
            y = type.radius
            velocityY = -velocityY
        } else if (y > 1f - type.radius) {
            y = 1f - type.radius
            velocityY = -velocityY
        }
    }

    fun contains(logicalX: Float, logicalY: Float): Boolean {
        val dx = logicalX - x
        val dy = logicalY - y
        return sqrt(dx * dx + dy * dy) <= type.radius * 1.25f
    }
}
