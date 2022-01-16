package com.kleidukos.seriescloud.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.backend.SeriesLoader
import com.kleidukos.seriescloud.adapter.StreamListItemAdapter
import com.kleidukos.seriescloud.backend.Series
import java.lang.Exception

class HomeFragment : Fragment() {

    private lateinit var containerLayout: RecyclerView
    private lateinit var searchBox: EditText
    private lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)

        containerLayout = root.findViewById(R.id.container_list)
        searchBox = root.findViewById(R.id.search_box)

        containerLayout.layoutManager = LinearLayoutManager(context)

        containerLayout.adapter = StreamListItemAdapter(
            root.context,
            SeriesLoader.ALL_SERIES as MutableList<Series>
        )

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let { text ->
                    val list: MutableList<Series> = mutableListOf()
                    list.addAll(SeriesLoader.ALL_SERIES.filter { it.name.contains(text, true) }.take(100))

                    containerLayout.removeAllViews()
                    containerLayout.adapter = StreamListItemAdapter(root.context, list)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        return root
    }
}