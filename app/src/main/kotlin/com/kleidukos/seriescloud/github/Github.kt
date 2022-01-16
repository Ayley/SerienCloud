package com.kleidukos.seriescloud.github

import android.content.Context
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

class Github {

    private val url:String = "https://api.github.com/repos/Ayley/SerienCloud/releases"
    private val userAgent:String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36 OPR/77.0.4054.172"

    suspend fun isNewerVersionAvailable(context: Context): String?{
        val newestReleaseItem = try {
            getNewestReleaseItem()
        }catch (e: Exception){
            return null
        }
        val newestReleaseVersion = newestReleaseItem.tag_name.replace(".", "").toInt()
        val currentAppVersion = getCurrentVersion(context)

        return if(newestReleaseVersion > currentAppVersion){
            newestReleaseItem.assets[0].browser_download_url + ";" + newestReleaseItem.body
        }else{
            null
        }
    }

    suspend fun getNewestReleaseItem(): ReleaseItem {
        val client = HttpClient()
        val response: String = client.get(url){
            headers{
                append(HttpHeaders.UserAgent, userAgent)
            }
        }

        val releases:List<ReleaseItem> = Json { ignoreUnknownKeys = true; coerceInputValues = true }.decodeFromString(response)

        return releases[0]
    }

    fun getCurrentVersion(context:Context):Int{
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName .replace(".","").toInt()
    }
}