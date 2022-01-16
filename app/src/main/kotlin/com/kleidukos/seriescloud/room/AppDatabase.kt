package com.kleidukos.seriescloud.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kleidukos.seriescloud.room.converter.HashMapConverter
import com.kleidukos.seriescloud.room.favourites.FavouriteSeries
import com.kleidukos.seriescloud.room.favourites.FavouriteSeriesDao
import com.kleidukos.seriescloud.room.seen.SeenSeries
import com.kleidukos.seriescloud.room.seen.SeenSeriesDao

@TypeConverters(HashMapConverter::class)
@Database(entities = [FavouriteSeries::class, SeenSeries::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun favourites(): FavouriteSeriesDao

    abstract fun seenSeries(): SeenSeriesDao

}