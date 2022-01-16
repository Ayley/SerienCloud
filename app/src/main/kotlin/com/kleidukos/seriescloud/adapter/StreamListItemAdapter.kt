package com.kleidukos.seriescloud.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.backend.Series
import com.kleidukos.seriescloud.backend.SeriesLoader
import com.kleidukos.seriescloud.room.favourites.FavouriteSeries
import com.kleidukos.seriescloud.ui.FavouriteFragment
import com.kleidukos.seriescloud.ui.HomeActivity
import com.kleidukos.seriescloud.ui.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class StreamListItemAdapter(val context: Context, val series: MutableList<Series>) :
    RecyclerView.Adapter<StreamListItemAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.genre_item_button)
        val icon: ImageView = view.findViewById(R.id.list_item_favourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val service = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = service.inflate(R.layout.stream_list_item, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = series[position]

        holder.title.text = item.name

        holder.title.setOnClickListener {
            Toast.makeText(context, "Lade Stream...", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.Main).launch {
                item.load()
                SeriesLoader.currentSeries = item
                holder.itemView.findNavController().navigate(R.id.navigation_stream)
            }
        }

        holder.title.setOnLongClickListener {
            val list = HomeActivity.database.favourites().getList()

            if(list.any { s -> s.title == item.name }){
                val fav = list.first { i -> i.title == item.name }

                HomeActivity.database.favourites().removeFavourite(fav)

                Toast.makeText(context, "Serie wurde aus den Favoriten entfernt...", Toast.LENGTH_LONG).show()

                holder.icon.visibility = View.GONE

                try {
                    val fm = holder.itemView.findFragment<FavouriteFragment>()
                    fm.removeSeries(item)
                }catch (e: Exception){
                    //Nothing
                }

                return@setOnLongClickListener true
            }

            val id = list.size

            val favourite = FavouriteSeries(id, item.name, item.link, item.genre)

            HomeActivity.database.favourites().insertFavourite(favourite)

            Toast.makeText(context, "Serie wurde zu den Favoriten hinzugefügt...", Toast.LENGTH_LONG).show()

            holder.icon.visibility = View.VISIBLE

            return@setOnLongClickListener true
        }

        val list = HomeActivity.database.favourites().getList()

        if(list.any { s -> s.title == item.name} ){
            holder.icon.visibility = View.VISIBLE
        }else{
            holder.icon.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return series.size
    }
}