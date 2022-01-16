package com.kleidukos.seriescloud.backend

import com.tfowl.ktor.client.features.JsoupFeature
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

data class Season(val link: String, val number: Int) {

    lateinit var episodes: List<Episode>
        private set

    var isLoaded: Boolean = false
        private set

    suspend fun load(){
        if(isLoaded){
            return
        }

        val episodeList: MutableList<Episode> = mutableListOf()
        HttpClient(){
            install(JsoupFeature){
                parsers[ContentType.Text.Html] = Parser.htmlParser()
            }
        }.use { client ->
            val doc = client.get<Document>(link)

            val list = doc.selectFirst("tbody#season$number")!!.children()

            for (item in list){
                val germanTitle: String? = item.selectFirst("strong")?.text()
                val englishTitle: String? = item.selectFirst("span")?.text()
                val link: String = "https://www.serien.sx" + item.selectFirst("a")!!.attr("href")
                val id: Int = item.attr("data-episode-season-id").toInt()

                episodeList.add(Episode(germanTitle, englishTitle, link, id))
            }
        }

        episodes = episodeList
        isLoaded = true
    }

}