package com.example.gamezhukii

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var engine: GameEngine? = null
    private var running = false
    private var lastFrameNanos = 0L

    var onStatsChanged: ((GameStats) -> Unit)? = null
    var onRoundFinished: ((GameStats) -> Unit)? = null

    fun start(config: GameConfig) {
        engine = GameEngine(config).also { it.reset() }
        running = true
        lastFrameNanos = 0L
        onStatsChanged?.invoke(engine!!.stats())
        postInvalidateOnAnimation()
    }

    fun stop() {
        running = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawField(canvas)

        val currentEngine = engine ?: return
        if (running) {
            val now = System.nanoTime()
            if (lastFrameNanos != 0L) {
                val delta = ((now - lastFrameNanos) / 1_000_000_000f).coerceAtMost(0.05f)
                currentEngine.update(delta)
            }
            lastFrameNanos = now
        }

        currentEngine.bugs.forEach { drawBug(canvas, it) }
        onStatsChanged?.invoke(currentEngine.stats())

        if (running && currentEngine.isFinished) {
            running = false
            onRoundFinished?.invoke(currentEngine.stats())
        } else if (running) {
            postInvalidateOnAnimation()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN || !running || width == 0 || height == 0) {
            return true
        }
        engine?.tap(event.x / width, event.y / height)
        engine?.stats()?.let { onStatsChanged?.invoke(it) }
        performClick()
        invalidate()
        return true
    }

    override fun performClick(): Boolean = super.performClick()

    override fun onDetachedFromWindow() {
        stop()
        super.onDetachedFromWindow()
    }

    private fun drawField(canvas: Canvas) {
        canvas.drawColor(Color.rgb(238, 247, 232))
        paint.color = Color.rgb(198, 222, 184)
        paint.strokeWidth = 2f
        val step = width / 8f
        var x = step
        while (x < width) {
            canvas.drawLine(x, 0f, x, height.toFloat(), paint)
            x += step
        }
    }

    private fun drawBug(canvas: Canvas, bug: GameBug) {
        val centerX = bug.x * width
        val centerY = bug.y * height
        val radius = bug.type.radius * minOf(width, height)
        val angle = Math.toDegrees(atan2(bug.velocityY, bug.velocityX).toDouble()).toFloat() + 90f

        canvas.save()
        canvas.rotate(angle, centerX, centerY)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = radius * 0.12f
        paint.color = Color.rgb(55, 43, 32)
        repeat(3) { index ->
            val offset = radius * (index - 1) * 0.55f
            canvas.drawLine(centerX - radius * 0.75f, centerY + offset, centerX - radius * 1.2f, centerY + offset + radius * 0.35f, paint)
            canvas.drawLine(centerX + radius * 0.75f, centerY + offset, centerX + radius * 1.2f, centerY + offset + radius * 0.35f, paint)
        }
        canvas.drawLine(centerX - radius * 0.3f, centerY - radius, centerX - radius * 0.7f, centerY - radius * 1.45f, paint)
        canvas.drawLine(centerX + radius * 0.3f, centerY - radius, centerX + radius * 0.7f, centerY - radius * 1.45f, paint)

        paint.style = Paint.Style.FILL
        paint.color = when (bug.type) {
            BugType.NORMAL -> Color.rgb(94, 72, 52)
            BugType.FAST -> Color.rgb(198, 48, 43)
            BugType.RARE -> Color.rgb(255, 193, 7)
        }
        canvas.drawOval(RectF(centerX - radius, centerY - radius * 1.15f, centerX + radius, centerY + radius * 1.15f), paint)

        paint.color = Color.rgb(35, 30, 25)
        canvas.drawCircle(centerX, centerY - radius * 0.85f, radius * 0.48f, paint)
        if (bug.type == BugType.RARE) {
            paint.color = Color.WHITE
            canvas.drawCircle(centerX - radius * 0.35f, centerY, radius * 0.16f, paint)
            canvas.drawCircle(centerX + radius * 0.35f, centerY, radius * 0.16f, paint)
        }
        canvas.restore()
    }
}
