package com.kleidukos.seriescloud.adapter

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.kleidukos.seriescloud.ui.StreamFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SeasonSelectionSpinnerAdapter(val streamFragment: StreamFragment): AdapterView.OnItemSelectedListener {

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val item = streamFragment.currentSeries.seasons[p2]

        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(streamFragment.context, "Lade Staffel...", Toast.LENGTH_SHORT).show()
            item.load()
            streamFragment.seasonList.adapter = EpisodeItemAdapter(streamFragment.requireContext(), item.episodes, item.number, streamFragment.currentSeries)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}