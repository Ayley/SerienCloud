package com.kleidukos.seriescloud.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import com.kleidukos.seriescloud.backend.SeriesLoader
import com.kleidukos.seriescloud.ui.PlayerActivity

class StreamSelectionAdapter(val context: Context, private val playerActivity: PlayerActivity): AdapterView.OnItemSelectedListener {

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val hoster = playerActivity.episode.streams.first { it.language == playerActivity.language}.hoster[p2]
        playerActivity.loadStream(hoster)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        p0!!.setSelection(0)
    }
}