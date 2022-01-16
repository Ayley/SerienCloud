package com.kleidukos.seriescloud.util

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.kleidukos.seriescloud.BuildConfig

class DownloadService(val url: String, val activity: Activity) {

    private var path: String
    private var uri: Uri

    init {
        var destination =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/"
        val fileName = "SerienCloud.apk"
        destination += fileName

        path = destination
        uri = Uri.parse("file://$path")
    }

    fun startDownload() {
        val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = createRequest()
        val receiver = createBroadcastReceiver()
        activity.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        downloadManager.enqueue(request)
    }

    private fun createRequest(): DownloadManager.Request {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("SerienCloud update")
        request.setDescription("Update wird heruntergeladen...")
        request.setMimeType("application/vnd.android.package-archive")
        request.setDestinationUri(uri)
        return request
    }

    private fun createBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val contentUri = FileProvider.getUriForFile(
                    activity.applicationContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    uri.toFile()
                )
                val install = Intent(Intent.ACTION_VIEW)
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE,true)
                install.data = contentUri
                activity.startActivity(install)

                activity.unregisterReceiver(this)
            }
        }
    }
}