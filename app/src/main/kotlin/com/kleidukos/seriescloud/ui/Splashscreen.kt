package com.kleidukos.seriescloud.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.backend.SeriesLoader
import com.kleidukos.seriescloud.github.Github
import com.kleidukos.seriescloud.util.DownloadService
import com.kleidukos.seriescloud.util.NetworkVPNAvailability
import kotlinx.coroutines.*

class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val versionText = findViewById<TextView>(R.id.textView)
        versionText.text = "Version " + getCurrentVersion(applicationContext)

        val vpn = checkForVPN()

        CoroutineScope(Dispatchers.Main).launch {
            if (vpn) {
                checkForAppVerison()

                Log.d("Loading", "Load all series")
                SeriesLoader.load()

                val intent = Intent(this@Splashscreen, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkForVPN(): Boolean {
        Log.d("Loading", "Check for VPN")
        val connectivityManager: ConnectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (!NetworkVPNAvailability.isVpnAvailable(connectivityManager)) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@Splashscreen)
                    .setTitle("Kein VPN verbunden")
                    .setMessage("Für die sichere nutzung der App wird ein VPN benötigt. Blokada Slim ist eine kostenlose App welche einen VPN bereitstellt. \nDie App wird sich nach dem Bestätigen schließen.")
                    .setPositiveButton("OK") { _, _ ->
                        finish()
                    }
                    .show()
            }
            Log.d("Loading", "Enable VPN")
            return false
        }
        Log.d("Loading", "VPN is avilable")
        return true
    }

    private suspend fun checkForAppVerison() {
        Log.d("Loading", "Check for update")
        Github().isNewerVersionAvailable(applicationContext)?.let {
            var splitter = it.split(";")
            var link = splitter[0]
            var description = splitter[1]

            val downloadService = DownloadService(link, this)

            runOnUiThread {
                MaterialAlertDialogBuilder(this@Splashscreen)
                    .setTitle("Neues update verfügbar!")
                    .setMessage(description + "\n\n Neues update wird für das nutzen der App benötigt.")
                    .setPositiveButton("OK") {
                        _, _ ->
                        downloadService.startDownload()
                    }.show()
            }
        }
        Log.d("Loading", "No update found")
    }

    private fun getCurrentVersion(context: Context): String {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }
}