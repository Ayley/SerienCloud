package com.kleidukos.seriescloud.room.converter

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.HashMap

class HashMapConverter {

    @TypeConverter
    fun toSource(data: String?): HashMap<Int, List<Int>> {
        if (data == null) {
            return hashMapOf()
        }
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun fromSource(map: HashMap<Int, List<Int>>): String {
        return Json.encodeToString(map)
    }

}