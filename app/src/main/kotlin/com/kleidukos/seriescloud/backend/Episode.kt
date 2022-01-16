package com.kleidukos.seriescloud.backend

import com.tfowl.ktor.client.features.JsoupFeature
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

data class Episode(val germanTitle: String?, val englishTitle: String?, val link: String, val number: Int) {

    var isLoaded = false

    lateinit var streams: List<Stream>
        private set

    suspend fun load(){
        HttpClient(){
            install(JsoupFeature){
                parsers[ContentType.Text.Html] = Parser.htmlParser()
            }
        }.use { client ->
            val doc = client.get<Document>(link)

            val languagesImages = doc.selectFirst("div.changeLanguageBox")!!.select("img")

            val streamList = doc.selectFirst("ul.row")!!.children()

            val list: MutableList<Stream> = mutableListOf()

            for (item in languagesImages){
                val language = if(item.attr("title") == "mit deutschen Untertiteln"){
                    "Ger-Sub"
                }else{
                    item.attr("title")
                }

                val langKey = item.attr("data-lang-key")

                val streamItems = streamList.filter { it.attr("data-lang-key") == langKey }

                val hosters: MutableList<Hoster> = mutableListOf()

                for (hoster in streamItems){
                    val title = hoster.selectFirst("h4")!!.text()
                    if(title.equals("VideoVard", true)){
                        continue
                    }
                    val redirect = "https://serien.sx" + hoster.attr("data-link-target")

                    hosters.add(Hoster(title, redirect))
                }
                list.add(Stream(language, hosters))
            }

            streams = list

            isLoaded = true
        }
    }

}