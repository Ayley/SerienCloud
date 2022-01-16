package com.kleidukos.seriescloud.backend

import android.util.Log
import com.tfowl.ktor.client.features.JsoupFeature
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

class SeriesLoader {

    companion object{

        private var domain: String = "https://www.serien.sx"

        var currentSeries: Series? = null

        lateinit var ALL_SERIES: List<Series>
        lateinit var NEW_SERIES: List<Series>
        lateinit var POPULAR_SERIES: List<Series>

        fun load(){
            runBlocking {
                loadAllSeries()
                loadNewSeries()
                loadPopularSeries()
            }
        }

        private suspend fun loadAllSeries(){
            val tempList: MutableList<Series> = mutableListOf()
            HttpClient() {
                install(JsoupFeature){
                    parsers[ContentType.Text.Html] = Parser.htmlParser()
                }
            }.use { client ->
                val doc = client.get<Document>("$domain/serien")

                val list = doc.selectFirst("div.seriesList")!!.children()

                for (element in list){
                    var genre: Genre = Genre.byName(element.children()[0].text())

                    var series = element.children()[1].children()

                    for (serie in series){
                        var name: String = serie.text()

                        var link: String = serie.selectFirst("a")!!.attr("href")

                        tempList.add(Series(name,"https://www.serien.sx$link", genre))
                    }
                }
            }
            ALL_SERIES = tempList
        }

        private suspend fun loadNewSeries(){
            val tempList: MutableList<Series> = mutableListOf()
            HttpClient() {
                install(JsoupFeature){
                    parsers[ContentType.Text.Html] = Parser.htmlParser()
                }
            }.use { client ->
                val doc = client.get<Document>("$domain/neu")

                val list = doc.selectFirst("div.seriesListContainer")!!.children()

                for (element in list){
                    val link: String = element.children()[0].attr("href")

                    val genre: Genre = Genre.byName(element.selectFirst("small")!!.text())

                    val name: String = element.selectFirst("h3")!!.text().replace("\"", "")

                    val imgSrc: String = element.selectFirst("img")!!.attr("src")

                    tempList.add(Series(name,"https://www.serien.sx$link", genre))
                }
            }
            NEW_SERIES = tempList
        }

        private suspend fun loadPopularSeries(){
            val tempList: MutableList<Series> = mutableListOf()
            HttpClient() {
                install(JsoupFeature){
                    parsers[ContentType.Text.Html] = Parser.htmlParser()
                }
            }.use { client ->
                val doc = client.get<Document>("$domain/beliebte-serien")

                val list = doc.selectFirst("div.seriesListContainer")!!.children()

                for (element in list){
                    val link: String = element.children()[0].attr("href")

                    val genre: Genre = Genre.byName(element.selectFirst("small")!!.text())

                    val name: String = element.selectFirst("h3")!!.text().replace("\"", "")

                    val imgSrc: String = element.selectFirst("img")!!.attr("src")

                    tempList.add(Series(name, "https://www.serien.sx$link", genre))
                }
            }
            POPULAR_SERIES = tempList
        }
    }

}