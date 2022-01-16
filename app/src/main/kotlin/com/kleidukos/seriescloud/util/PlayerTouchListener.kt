package com.kleidukos.seriescloud.util

import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.kleidukos.seriescloud.ui.PlayerActivity

class PlayerTouchListener(
    val ctx: Context,
    val volume: SeekBar,
    val volumeContainer: ConstraintLayout,
    val brightness: SeekBar,
    val brightnessContainer: ConstraintLayout,
    val streamVideoPlayer: PlayerActivity
) : OnSwipeTouchListener(ctx) {

    private val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun onSwipeTop(event: MotionEvent) {
        if (event.x < ctx.resources.displayMetrics.widthPixels / 2) {
            brightness.progress =
                Settings.System.getInt(ctx.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            brightnessContainer.visibility = View.VISIBLE
            streamVideoPlayer.count = 3

            adjustBrightness(255 / 15)
        } else {
            volume.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            volumeContainer.visibility = View.VISIBLE
            streamVideoPlayer.count = 3

            adjustVolume(1)
        }
    }

    override fun onSwipeBottom(event: MotionEvent) {
        if (event.x < ctx.resources.displayMetrics.widthPixels / 2) {
            brightness.progress =
                Settings.System.getInt(ctx.contentResolver, Settings.System.SCREEN_BRIGHTNESS)

            brightnessContainer.visibility = View.VISIBLE
            streamVideoPlayer.count = 3

            adjustBrightness(-(255 / 15))
        } else {
            volume.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            volumeContainer.visibility = View.VISIBLE
            streamVideoPlayer.count = 3

            adjustVolume(-1)
        }
    }

    private fun adjustBrightness(amount: Int) {
        val current = brightness.progress

        setBrightness(current + amount)

        brightness.progress = current + amount
    }

    private fun adjustVolume(amount: Int) {
        val current = volume.progress

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            current + amount,
            AudioManager.FLAG_VIBRATE
        )

        volume.progress = current + amount
    }

    fun setBrightness(brightness: Int) {
        if(!streamVideoPlayer.getSharedPreferences("Settings", Context.MODE_PRIVATE).getBoolean("SetBrightness", false)){
            return
        }
        if (Settings.System.canWrite(streamVideoPlayer.applicationContext)) {
            val cResolver = streamVideoPlayer.applicationContext.contentResolver;
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        }
    }
}
