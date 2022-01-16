package com.kleidukos.seriescloud.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.github.Github
import com.kleidukos.seriescloud.util.DownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var root: View
    private lateinit var autoplay: Switch
    private lateinit var brightness: Switch

    private lateinit var update: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_settings, container, false)

        autoplay = root.findViewById(R.id.settings_autoplay)
        brightness = root.findViewById(R.id.settings_brightness)
        update = root.findViewById(R.id.settings_update)




        setAutoplaySwitch()
        setBrightnessSwitch()
        setUpdateClick()

        return root
    }

    private fun setBrightnessSwitch() {
        val brightnessCurrentState = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).getBoolean("SetBrightness", false)

        brightness.isChecked = brightnessCurrentState

        brightness.setOnCheckedChangeListener { switch, b ->
            requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().putBoolean("SetBrightness", b).apply()
            checkWriteSettingsPermission()
        }
    }

    private fun setAutoplaySwitch() {
        val autoplayCurrentState = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).getBoolean("Autoplay", true)

        autoplay.isChecked = autoplayCurrentState

        autoplay.setOnCheckedChangeListener { switch, b ->
            requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().putBoolean("Autoplay", b).apply()
        }
    }

    private fun setUpdateClick() {
        update.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                checkForAppVerison()
            }
        }
    }


    private suspend fun checkForAppVerison() {
        Log.d("Loading", "Check for update")
        Github().isNewerVersionAvailable(requireContext())?.let {
            var splitter = it.split(";")
            var link = splitter[0]
            var description = splitter[1]

            val downloadService = DownloadService(link, requireActivity())

            requireActivity().runOnUiThread {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Neues update verfügbar!")
                    .setMessage(description + "\n\n Neues update wird für das nutzen der App benötigt.")
                    .setPositiveButton("OK") {
                            _, _ ->
                        downloadService.startDownload()
                    }.show()
            }
        }
        Log.d("Loading", "No update found")
        Toast.makeText(requireContext(), "Kein update gefunden", Toast.LENGTH_SHORT).show()
    }

    private fun checkWriteSettingsPermission() {
        if(requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).getBoolean("SetBrightness", false)){
            if(!Settings.System.canWrite(requireContext())){
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.data = Uri.parse("package:${requireActivity().packageName}");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
            }
        }
    }
}