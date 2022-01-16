package com.kleidukos.seriescloud.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.backend.Episode
import com.kleidukos.seriescloud.backend.Series
import com.kleidukos.seriescloud.backend.SeriesLoader
import com.kleidukos.seriescloud.room.seen.SeenSeries
import com.kleidukos.seriescloud.ui.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpisodeItemAdapter(val context: Context, val episodes: List<Episode>, val seasonNumber: Int, val series: Series) :
    RecyclerView.Adapter<EpisodeItemAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.episode_number)
        val germanTitle: TextView = view.findViewById(R.id.episode_german_title)
        val englishTitle: TextView = view.findViewById(R.id.episode_english_title)

        val languageSelection: ConstraintLayout = view.findViewById(R.id.episode_language)
        val episodeTitles: ConstraintLayout = view.findViewById(R.id.episode_titles)

        val languages: Spinner = view.findViewById(R.id.episode_language_selection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val service = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = service.inflate(R.layout.episode_item, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = episodes[position]

        handleSeenBackground(holder, item)

        holder.number.text = item.number.toString()
        item.germanTitle?.let { holder.germanTitle.text = it }
        item.englishTitle?.let { holder.englishTitle.text = it }

        if(item.englishTitle == null){
            holder.englishTitle.text = ""
        }

        if(item.germanTitle == null){
            holder.englishTitle.text = ""
        }

        holder.episodeTitles.setOnClickListener {
            if(!item.isLoaded){
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Lade Streams...", Toast.LENGTH_SHORT).show()
                    item.load()

                    val list: MutableList<String> = mutableListOf()

                    list.add("Wähle eine sprache aus.")
                    item.streams.forEach {
                        list.add(it.language)
                    }

                    holder.languages.adapter = ArrayAdapter(context, R.layout.spinner_item, list)
                    holder.languages.setSelection(0)
                    holder.languages.onItemSelectedListener = EpisodeLanguageSelectionAdapter(context, item, seasonNumber, series)
                }
            }
            holder.episodeTitles.visibility = View.GONE
            holder.languageSelection.visibility = View.VISIBLE
        }

        holder.languageSelection.setOnClickListener {
            holder.episodeTitles.visibility = View.VISIBLE
            holder.languageSelection.visibility = View.GONE
        }

        holder.episodeTitles.setOnLongClickListener {
            handleSeen(item)
            handleSeenBackground(holder, item)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    fun handleSeenBackground(holder: Holder, episode: Episode){
        val list = HomeActivity.database.seenSeries().getList()
        if(list.any { it.title == series.name }){
            val item = list.first { it.title == series.name }

            if(item.info.containsKey(seasonNumber)){
                val map = item.info
                val episodes = map[seasonNumber]!!
                if(!episodes.contains(episode.number)){
                    holder.itemView.background = ResourcesCompat.getDrawable(holder.itemView.resources, R.drawable.genre_item_shape, null)
                }else{
                    holder.itemView.background = ResourcesCompat.getDrawable(holder.itemView.resources, R.drawable.genre_item_selected_shape, null)
                }
            }else{
                holder.itemView.background = ResourcesCompat.getDrawable(holder.itemView.resources, R.drawable.genre_item_shape, null)
            }
        }else{
           holder.itemView.background = ResourcesCompat.getDrawable(holder.itemView.resources, R.drawable.genre_item_shape, null)
        }
    }

    fun handleSeen(episode: Episode){
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
                }else{
                    val mutableList: MutableList<Int> = mutableListOf()
                    mutableList.addAll(episodes)
                    mutableList.remove(episode.number)

                    map[seasonNumber] = mutableList

                    HomeActivity.database.seenSeries().updateSeenMap(series.name, map)
                }
            }else{
                val map = item.info
                val episodes = mutableListOf<Int>()
                episodes.add(episode.number)
                map[seasonNumber] = episodes

                HomeActivity.database.seenSeries().insertSeenSeries(SeenSeries(series.name, map))
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