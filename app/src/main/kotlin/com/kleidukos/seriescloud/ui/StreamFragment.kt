package com.kleidukos.seriescloud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.adapter.SeasonSelectionSpinnerAdapter
import com.kleidukos.seriescloud.backend.Series
import com.kleidukos.seriescloud.backend.SeriesLoader

class StreamFragment : Fragment() {

    private lateinit var root: View
    lateinit var currentSeries: Series
        private set

    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var seasonSelection: Spinner
    lateinit var seasonList: RecyclerView
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_stream, container, false)

        title = root.findViewById(R.id.stream_title)
        description = root.findViewById(R.id.stream_description)
        seasonSelection = root.findViewById(R.id.stream_season_selector)
        seasonList = root.findViewById(R.id.stream_season_list)

        seasonList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        currentSeries = SeriesLoader.currentSeries!!

        title.text = currentSeries.name
        description.text = currentSeries.description

        seasonSpinner()

        return root
    }

    private fun seasonSpinner() {
        val spinnerItems: MutableList<String> = mutableListOf()

        var index: Int = 0

        for (item in currentSeries.seasons){
            val name: String = if(item.number == 0){
                index = if(currentSeries.seasons.size > 1){
                    1
                }else{
                    0
                }
                "Filme"
            }else{
                index = if(currentSeries.seasons[0].number == 0){
                    1
                }else{
                    0
                }
                "Staffel ${item.number}"
            }
            spinnerItems.add(name)
        }

        seasonSelection.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, spinnerItems)
        seasonSelection.onItemSelectedListener = SeasonSelectionSpinnerAdapter(this)
        seasonSelection.setSelection(index)
    }

    fun reloadItem(item: Int){
        seasonList.adapter?.notifyItemChanged(item)
    }
}