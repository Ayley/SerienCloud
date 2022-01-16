package com.kleidukos.seriescloud.room.favourites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kleidukos.seriescloud.backend.Genre
import com.kleidukos.seriescloud.backend.Series

@Entity
data class FavouriteSeries(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val title: String,
    @ColumnInfo(name = "link") val link: String,
    @ColumnInfo(name = "genre") val genre: Genre
) {

    fun getSeries(): Series{
        return Series(title, link, genre)
    }
}