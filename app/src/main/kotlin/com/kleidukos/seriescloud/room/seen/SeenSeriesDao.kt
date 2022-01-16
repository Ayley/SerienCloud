package com.kleidukos.seriescloud.room.seen

import androidx.room.*

@Dao
interface SeenSeriesDao {

    @Query("SELECT * FROM SeenSeries")
    fun getList(): List<SeenSeries>

    @Insert
    fun insertSeenSeries(seenSeries: SeenSeries)

    @Delete
    fun removeSeenSeries(seenSeries: SeenSeries)

    @Query("UPDATE SeenSeries SET info=:map WHERE title=:title")
    fun updateSeenMap(title: String, map: HashMap<Int, List<Int>>)
}