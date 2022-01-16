package com.kleidukos.seriescloud.tmdb

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


class TMDBManager {

    companion object{
        private val key: String = "009e8284b6f97c638e0face5f5400c1b"

        suspend fun loadResult(name: String, altName: String?): MultiResult?{

            var result = filterResult(name)

            if (result == null && altName != null){
                result = filterResult(name)
            }

            return validateResults(result, name) ?: altName?.let { validateResults(result, it) }
        }

        private fun validateResults(results: List<MultiResult>, name: String): MultiResult?{
            return results.firstOrNull { it.name?.contains(name, true) == true || it.originalName?.contains(name, true) == true || it.title?.contains(name, true) == true || it.originalTitle?.contains(name, true) == true }
        }

        private suspend fun filterResult(name: String): List<MultiResult>{
            val res = loadMultiResult(name)

            return res.results.filter { it.mediaType == "movie" || it.mediaType == "tv"}
        }

        private suspend fun loadMultiResult(name: String): MultiPage {
            val url = "https://api.themoviedb.org/3/search/multi?api_key=" + key + "&query=" + URLEncoder.encode(name, StandardCharsets.UTF_8.name()) + "&language=de";

            HttpClient(){
            }.use { client ->
                val json = client.get<String>(url)

                return Json { ignoreUnknownKeys = true; coerceInputValues = true; isLenient = true}.decodeFromString(json)
            }
        }
    }
}