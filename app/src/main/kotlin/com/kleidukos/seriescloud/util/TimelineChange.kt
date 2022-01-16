package com.kleidukos.seriescloud.util

import android.widget.SeekBar
import com.kleidukos.seriescloud.ui.PlayerActivity

class TimelineChange(private val playerActivity: PlayerActivity): SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        playerActivity.playedTime.text = playerActivity.formatTime(p1)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        playerActivity.isTimelineInUse = true
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        playerActivity.isTimelineInUse = false

        playerActivity.count = 5

        playerActivity.videoView.seekTo(p0!!.progress)
        playerActivity.videoView.start()
    }
}