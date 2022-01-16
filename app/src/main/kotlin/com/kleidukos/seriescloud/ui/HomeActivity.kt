package com.kleidukos.seriescloud.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.room.Room
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.room.AppDatabase

class HomeActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private var isFragmentActive = false

    companion object{
        lateinit var database: AppDatabase
    }

    private fun setDatabase(){
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "SerienCloud")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_menu_foreground)

        supportActionBar?.title = "SerienCloud"

        setDatabase()

        checkWriteSettingsPermission()
    }

    private fun checkWriteSettingsPermission() {
        if(getSharedPreferences("Settings", Context.MODE_PRIVATE).getBoolean("SetBrightness", false)){
            if(!Settings.System.canWrite(applicationContext)){
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.data = Uri.parse("package:$packageName");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(isFragmentActive){
            return true
        }
        return when (item.itemId) {
            R.id.action_favourite -> {
                findNavController(R.id.fragment_container).navigate(R.id.navigation_favourite)
                isFragmentActive = true
                true
            }
            R.id.action_settings -> {
                findNavController(R.id.fragment_container).navigate(R.id.navigation_settings)
                isFragmentActive = true
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        isFragmentActive = false
    }
}