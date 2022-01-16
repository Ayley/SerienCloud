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
import com.kleidukos.seriescloud.adapter.StreamListItemAdapter
import androidx.core.view.size
import com.kleidukos.seriescloud.backend.Series


class FavouriteFragment : Fragment() {

    private lateinit var root: View
    private lateinit var searchBox: EditText
    private lateinit var resultList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_favourite, container, false)

        searchBox = root.findViewById(R.id.search_box)
        resultList = root.findViewById(R.id.search_list)

        resultList.layoutManager = LinearLayoutManager(root.context)

        val list: MutableList<Series> = mutableListOf()

        HomeActivity.database.favourites().getList().forEach {
            list.add(it.getSeries())
        }

        if(resultList.size == 0){
            resultList.adapter = StreamListItemAdapter(root.context, list)
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let { text ->
                    val list: MutableList<Series> = mutableListOf()

                    HomeActivity.database.favourites().getList().forEach {
                        list.add(it.getSeries())
                    }

                    resultList.removeAllViews()
                    resultList.adapter = StreamListItemAdapter(root.context,
                        list.filter { it.name.contains(text , true)} as MutableList<Series>)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        return root
    }

    fun removeSeries(series: Series){
        val adapter = resultList.adapter as StreamListItemAdapter

        adapter.series.remove(series)
    }
}