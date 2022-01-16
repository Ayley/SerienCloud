package com.kleidukos.seriescloud.util

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.kleidukos.seriescloud.ui.PlayerActivity
import kotlinx.android.synthetic.main.activity_videoplayer.*

class CustomWebViewClient(private val playerActivity: PlayerActivity): WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        view?.visibility = View.INVISIBLE
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        Log.d("TEST", url!!)
        if (url.contains("redirect")) {
            view?.visibility = View.VISIBLE
            playerActivity.loading_container.visibility = View.GONE
        } else {
            view?.visibility = View.GONE
            playerActivity.loading_container.visibility = View.VISIBLE
            view?.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
        }
    }
}