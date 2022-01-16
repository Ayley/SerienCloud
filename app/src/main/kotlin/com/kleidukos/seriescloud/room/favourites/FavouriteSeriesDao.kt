package com.kleidukos.seriescloud.room.favourites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavouriteSeriesDao {

    @Query("SELECT * FROM FavouriteSeries")
    fun getList(): List<FavouriteSeries>

    @Insert
    fun insertFavourite(favouriteSeries: FavouriteSeries)

    @Delete
    fun removeFavourite(favouriteSeries: FavouriteSeries)
}