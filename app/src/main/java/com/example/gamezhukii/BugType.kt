package com.example.gamezhukii

enum class BugType(
    val speed: Float,
    val radius: Float,
    val points: Int
) {
    NORMAL(speed = 0.12f, radius = 0.065f, points = 10),
    FAST(speed = 0.24f, radius = 0.045f, points = 20),
    RARE(speed = 0.16f, radius = 0.10f, points = 50)
}
