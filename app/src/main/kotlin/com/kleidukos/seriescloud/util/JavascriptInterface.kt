package com.kleidukos.seriescloud.util

import android.util.Log
import android.webkit.JavascriptInterface
import com.kleidukos.seriescloud.ui.PlayerActivity

class JavaScriptInterface(val hoster: String, val playerActivity: PlayerActivity) {

    var loaded = false

    @JavascriptInterface
    fun processHTML(html: String?) {
        Log.d("MYHTML", html!!)

        when (hoster) {

            "VOE" -> {
                var src = VideoSourceParser.voe(html) ?: ""
                Log.d("MYSRC", src)
                if (!loaded) {
                    loaded = true
                    playerActivity.runOnUiThread {
                        playerActivity.playStream(src)
                    }
                }
            }
            "Streamtape" -> {
                var src = VideoSourceParser.streamtape(html) ?: ""
                Log.d("MYSRC", src)
                if (!loaded) {
                    loaded = true
                    playerActivity.runOnUiThread {
                        playerActivity.playStream(src)
                    }
                }
            }
            "Vidoza" -> {
                var src = VideoSourceParser.vidoza(html) ?: ""
                Log.d("MYSRC", src)
                if (!loaded) {
                    loaded = true
                    playerActivity.runOnUiThread {
                        playerActivity.playStream(src)
                    }
                }
            }
            "PlayTube" -> {
            }
        }
    }
}