package com.kleidukos.seriescloud.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.findFragment
import com.kleidukos.seriescloud.backend.Episode
import com.kleidukos.seriescloud.backend.Series
import com.kleidukos.seriescloud.room.seen.SeenSeries
import com.kleidukos.seriescloud.ui.FavouriteFragment
import com.kleidukos.seriescloud.ui.HomeActivity
import com.kleidukos.seriescloud.ui.PlayerActivity
import com.kleidukos.seriescloud.ui.StreamFragment
import java.lang.Exception

class EpisodeLanguageSelectionAdapter(val context: Context, val episode: Episode, val seasonNumber: Int, val series: Series): AdapterView.OnItemSelectedListener {

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(p2 != 0){
            handleSeen()

            try {
                val fm = p0?.findFragment<StreamFragment>()
                fm?.reloadItem(episode.number -1)
            }catch (e: Exception){
                //Nothing
            }

            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("episode", episode.number)
            intent.putExtra("season", seasonNumber)
            intent.putExtra("language", p2-1)
            context.startActivity(intent)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    fun handleSeen(){
        val list = HomeActivity.database.seenSeries().getList()
        if(list.any { it.title == series.name }){
            val item = list.first { it.title == series.name }

            if(item.info.containsKey(seasonNumber)){
                val map = item.info
                val episodes = map[seasonNumber]!!
                if(!episodes.contains(episode.number)){
                    val mutableList: MutableList<Int> = mutableListOf()
                    mutableList.addAll(episodes)
                    mutableList.add(episode.number)

                    map[seasonNumber] = mutableList

                    HomeActivity.database.seenSeries().updateSeenMap(series.name, map)
                }
            }else{
                val map = item.info
                val episodes = mutableListOf<Int>()
                episodes.add(episode.number)
                map[seasonNumber] = episodes

                HomeActivity.database.seenSeries().updateSeenMap(series.name, map)
            }
        }else{
            val map = hashMapOf<Int, List<Int>>()
            val episodes = mutableListOf<Int>()
            episodes.add(episode.number)
            map[seasonNumber] = episodes

            HomeActivity.database.seenSeries().insertSeenSeries(SeenSeries(series.name, map))
        }
    }
}