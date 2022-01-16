package com.kleidukos.seriescloud.backend

import android.util.Log
import com.kleidukos.seriescloud.tmdb.TMDBManager
import com.tfowl.ktor.client.features.JsoupFeature
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

data class Series(val name: String, val link: String, val genre: Genre) {

    lateinit var description: String
        private set

    lateinit var seasons: List<Season>
        private set

    suspend fun load(){
        HttpClient(){
            install(JsoupFeature) {
                parsers[ContentType.Text.Html] = Parser.htmlParser()
            }
        }.use { client ->
            Log.d("TEST", link)
            val doc = client.get<Document>(link)

            description = doc.selectFirst("p.seri_des")!!.attr("data-full-description")

            val seasonsPanel = doc.selectFirst("div#stream")!!.selectFirst("ul")

            val seasonsCount = seasonsPanel!!.children()

            seasonsCount.removeAt(0)

            val season: MutableList<Season> = mutableListOf()

            for (node in seasonsCount){
                val id: Int = if(node.text() == "Filme"){
                    0
                }else{
                    node.text().toInt()
                }
                season.add(Season("$link/staffel-$id", id))
            }
            seasons = season
        }
    }
}