package com.kleidukos.seriescloud.room.seen

import androidx.room.*
import com.kleidukos.seriescloud.room.converter.HashMapConverter

@Entity
data class SeenSeries(
    @PrimaryKey val title: String,
    @TypeConverters(HashMapConverter::class)
    @ColumnInfo(name = "info") val info: HashMap<Int, List<Int>>
) {
}