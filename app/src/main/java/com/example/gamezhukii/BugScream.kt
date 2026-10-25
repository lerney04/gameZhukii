package com.example.gamezhukii

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

/** Plays the supplied insect recording for the duration of the tilt bonus. */
class BugScream(private val context: Context) {
    private var player: MediaPlayer? = null

    fun play() {
        if (player != null) return
        player = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
            context.resources.openRawResourceFd(R.raw.bonus_cricket).use { asset ->
                setDataSource(asset.fileDescriptor, asset.startOffset, asset.length)
            }
            isLooping = true
            prepare()
            start()
        }
    }

    fun release() {
        player?.release()
        player = null
    }
}
