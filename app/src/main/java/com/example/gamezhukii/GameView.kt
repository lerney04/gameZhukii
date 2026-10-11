package com.example.gamezhukii

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.sin

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bugBitmaps: Map<BugType, Bitmap> by lazy {
        mapOf(
            BugType.NORMAL to BitmapFactory.decodeResource(resources, R.drawable.bug_normal),
            BugType.FAST to BitmapFactory.decodeResource(resources, R.drawable.bug_fast),
            BugType.RARE to BitmapFactory.decodeResource(resources, R.drawable.bug_rare)
        )
    }
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
        canvas.drawColor(Color.rgb(255, 253, 231))
    }

    private fun drawBug(canvas: Canvas, bug: GameBug) {
        val centerX = bug.x * width
        val centerY = bug.y * height
        val radius = bug.type.radius * minOf(width, height)
        val angle = Math.toDegrees(atan2(bug.velocityY, bug.velocityX).toDouble()).toFloat() + 90f
        val wiggle = sin(System.nanoTime() / 70_000_000.0).toFloat() * 7f
        val bitmap = bugBitmaps.getValue(bug.type)
        val imageHeight = radius * 2.35f
        val imageWidth = imageHeight * bitmap.width / bitmap.height

        canvas.save()
        canvas.rotate(angle + wiggle, centerX, centerY)
        paint.style = Paint.Style.FILL
        canvas.drawBitmap(
            bitmap,
            null,
            RectF(
                centerX - imageWidth / 2f,
                centerY - imageHeight / 2f,
                centerX + imageWidth / 2f,
                centerY + imageHeight / 2f
            ),
            paint
        )
        canvas.restore()

    }
}
