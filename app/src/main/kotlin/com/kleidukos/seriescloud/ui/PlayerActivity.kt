package com.kleidukos.seriescloud.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.cast.framework.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.adapter.StreamSelectionAdapter
import com.kleidukos.seriescloud.backend.*
import com.kleidukos.seriescloud.util.CustomWebViewClient
import com.kleidukos.seriescloud.util.JavaScriptInterface
import com.kleidukos.seriescloud.util.PlayerTouchListener
import com.kleidukos.seriescloud.util.TimelineChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*




class PlayerActivity : AppCompatActivity() {

    private val series: Series = SeriesLoader.currentSeries!!
    internal lateinit var episode: Episode
    private lateinit var season: Season
    internal lateinit var language: String
    internal var isTimelineInUse: Boolean = false

    //loading container
    private lateinit var loadingContainer: ConstraintLayout

    //videoView webView
    internal lateinit var videoView: VideoView

    private lateinit var webView: WebView
    //player controls container
    private lateinit var controlContainer: ConstraintLayout

    //player controls top
    private lateinit var backArrow: ImageButton
    private lateinit var playerTitle: TextView
    private lateinit var providerSpinner: Spinner
    private lateinit var externalPlayers: ImageButton
    //player controls bottom
    internal lateinit var playedTime: TextView
    private lateinit var timeline: SeekBar
    private lateinit var playTime: TextView
    private lateinit var previousVideo: ImageButton
    private lateinit var skipBackward: ImageButton
    private lateinit var mediaControl: ImageButton
    private lateinit var skipForward: ImageButton
    private lateinit var nextVideo: ImageButton
    private lateinit var fullscreen: ImageButton

    //volume controls
    private lateinit var volumeContainer: ConstraintLayout

    private lateinit var volumeBar: SeekBar
    //brightness controls
    private lateinit var brightnessContainer: ConstraintLayout

    private lateinit var brightnessBar: SeekBar
    //variables
    internal var count: Int = 0
    internal var isFullscreen: Boolean = false

    private var looper: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoplayer)

        initSeries()
        setComponents()
        setStreamSpinner()
        setPlayerControls()
        setExternalPlayers()
        checkVisibilitySkipButtons()

        playerTitle.text = episode.germanTitle ?: episode.englishTitle
        count = 7
    }

    internal fun loadStream(hoster: Hoster){
        loadingContainer.visibility = View.VISIBLE

        videoView.stopPlayback()
        webView.addJavascriptInterface(JavaScriptInterface(hoster.name, this), "HTMLOUT")
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(hoster.link)

        webView.webViewClient = CustomWebViewClient(this)
    }

    internal fun playStream(link: String){
        videoView.setVideoURI(Uri.parse(link))
        videoView.start()
    }

    private fun setStreamSpinner() {
        val list: MutableList<String> = mutableListOf()

        episode.streams.first { it.language == language }.hoster.forEach { item ->
            list.add(item.name)
        }

        providerSpinner.adapter = ArrayAdapter(baseContext, R.layout.streams_spinner_item, list)
        providerSpinner.onItemSelectedListener = StreamSelectionAdapter(baseContext, this)
    }

    private suspend fun loadNextEpisode(){
        if (episode.number == season.episodes.size){
            if(season.number != series.seasons.size){
                season = series.seasons.first { it.number == season.number +1 }
                season.load()
                episode = season.episodes.first()
            }
        }else{
            episode = season.episodes[episode.number]
        }
        episode.load()
    }

    private suspend fun loadPreviousEpisode(){
        if (episode.number == season.episodes.first().number){
            if(season.number != series.seasons.first().number){
                season = series.seasons.first { it.number == season.number -1 }
                season.load()
                episode = season.episodes.last()
            }
        }else{
            episode = season.episodes[episode.number - 2]
        }
        episode.load()
    }

    private fun checkVisibilitySkipButtons(){
        if(season.number == series.seasons.first().number){
            if(episode.number == season.episodes.first().number){
                previousVideo.visibility = View.INVISIBLE
            }else{
                previousVideo.visibility = View.VISIBLE
            }
        }else{
            previousVideo.visibility = View.VISIBLE
        }

        if(season.number == series.seasons.last().number){
            if(episode.number == season.episodes.last().number){
                nextVideo.visibility = View.INVISIBLE
            }else{
                nextVideo.visibility = View.VISIBLE
            }
        }else{
            nextVideo.visibility = View.VISIBLE
        }
    }

    private fun setPlayerControls() {
        backArrow.setOnClickListener {
            onBackPressed()
        }

        fullscreen.setOnClickListener {
            toggleFullscreen()
            count = 5
        }

        controlContainer.setOnClickListener {
            if(controlContainer.visibility == View.VISIBLE){
                controlContainer.visibility = View.GONE
                count = 0
            }else{
                controlContainer.visibility = View.VISIBLE
                count = 5
            }
        }

        controlContainer.setOnTouchListener(PlayerTouchListener(applicationContext, volumeBar, volumeContainer, brightnessBar, brightnessContainer, this))

        videoView.setOnTouchListener(PlayerTouchListener(applicationContext, volumeBar, volumeContainer, brightnessBar, brightnessContainer, this))

        videoView.setOnClickListener {
            if(controlContainer.visibility == View.VISIBLE){
                controlContainer.visibility = View.GONE
                count = 0
            }else{
                controlContainer.visibility = View.VISIBLE
                count = 5
            }
        }

        skipBackward.setOnClickListener {
            videoView.seekTo(videoView.currentPosition - 10000)
            videoView.start()
            count = 5
        }

        skipForward.setOnClickListener {
            videoView.seekTo(videoView.currentPosition + 10000)
            videoView.start()
            count = 5
        }

        nextVideo.setOnClickListener {
            videoView.stopPlayback()
            loadingContainer.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                loadNextEpisode()
                setStreamSpinner()
                checkVisibilitySkipButtons()
                count = 5
            }
        }

        previousVideo.setOnClickListener {
            videoView.stopPlayback()
            loadingContainer.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.Main).launch {
                loadPreviousEpisode()
                setStreamSpinner()
                checkVisibilitySkipButtons()
                count = 5
            }
        }

        mediaControl.setOnClickListener {
            togglePlaying()
            count = 5
        }

        videoView.setOnPreparedListener {
            it.setOnInfoListener { mp, what, extra ->
                if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                    loadingContainer.visibility = View.VISIBLE
                }
                if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                    loadingContainer.visibility = View.GONE
                }
                return@setOnInfoListener false
            }


            videoView.setOnCompletionListener {
                if(getSharedPreferences("Settings", MODE_PRIVATE).getBoolean("Autoplay", true)){
                    CoroutineScope(Dispatchers.Main).launch {
                        loadNextEpisode()
                        setStreamSpinner()
                        checkVisibilitySkipButtons()
                    }
                }
            }

            playerTitle.text = episode.germanTitle ?: episode.englishTitle

            timeline.max = videoView.duration

            playTime.text = formatTime(videoView.duration)

            loadingContainer.visibility = View.GONE
        }

        timeline.setOnSeekBarChangeListener(TimelineChange(this))
    }

    private fun togglePlaying() {
        if (videoView.isPlaying) {
            videoView.pause()
            mediaControl.setImageResource(R.drawable.ic_play_button_foreground)
        } else {
            videoView.start()
            mediaControl.setImageResource(R.drawable.ic_pause_button_foreground)
        }
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            videoView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            fullscreen.setImageResource(R.drawable.ic_close_fullscreen_foreground)
            isFullscreen = false
        } else {
            videoView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            fullscreen.setImageResource(R.drawable.ic_open_fullscreen_foreground)
            isFullscreen = true
        }
    }

    private fun loopTask() = object : Runnable {
        override fun run() {
            if(count == 0){
                hideDecor()
                controlContainer.visibility = View.INVISIBLE
                volumeContainer.visibility = View.GONE
                brightnessContainer.visibility = View.GONE
                count = -1
            }

            if(count > 0){
                if(!isTimelineInUse){
                    count--
                }
            }

            if(!isTimelineInUse){
                runOnUiThread {
                    timeline.progress = videoView.currentPosition
                }
            }
            looper?.postDelayed(this,1000)
        }
    }

    private fun getVideoUri(): Uri {
        return try {
            val mUriField: Field = VideoView::class.java.getDeclaredField("mUri")
            mUriField.isAccessible = true
            mUriField.get(videoView) as Uri
        } catch (e: Exception) {
            Uri.EMPTY
        }
    }

    private fun setExternalPlayers() {
        externalPlayers.setOnClickListener {
            val uri = getVideoUri()

            if (uri.toString().isEmpty()) {
                Handler(Looper.getMainLooper()).post {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Externer Videoplayer")
                        .setMessage("Warte bis die Url geladen ist")
                        .setNeutralButton("OK") { _, _ ->
                            //Nothing
                        }.show()
                }
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "video/*")

                startActivity(Intent.createChooser(intent, "Wähle einen Player aus"))
            }
        }
    }

    private fun initSeries() {
        val episodeNumber = intent.extras!!.getInt("episode")
        val seasonNumber = intent.extras!!.getInt("season")

        series.seasons.forEach {
            if(it.number == seasonNumber){
                season = it
            }
        }

        episode = season.episodes.first { it.number == episodeNumber }

        language = episode.streams[intent.extras!!.getInt("language")].language
    }

    private fun setComponents() {
        //loading container
        loadingContainer = findViewById(R.id.loading_container)

        //videoView webView
        videoView = findViewById(R.id.videoplayer)
        webView = findViewById(R.id.webview)

        //player controls container
        controlContainer = findViewById(R.id.control_container)

        //player controls top
        backArrow = findViewById(R.id.back_arrow)
        playerTitle = findViewById(R.id.player_title)
        providerSpinner = findViewById(R.id.providers)
        externalPlayers = findViewById(R.id.external_player)

        //player controls bottom
        playedTime = findViewById(R.id.player_played_time)
        timeline = findViewById(R.id.time_bar)
        playTime = findViewById(R.id.player_play_time)
        previousVideo = findViewById(R.id.previous_video)
        skipBackward = findViewById(R.id.skip_backwards)
        mediaControl = findViewById(R.id.control_video)
        skipForward = findViewById(R.id.skip_forward)
        nextVideo = findViewById(R.id.next_video)
        fullscreen = findViewById(R.id.player_fullscreen)

        //volume controls
        volumeContainer = findViewById(R.id.volume_container)
        volumeBar = findViewById(R.id.volume_bar)

        //brightness controls
        brightnessContainer = findViewById(R.id.brightness_container)
        brightnessBar = findViewById(R.id.brightness_bar)
    }

    private fun hideDecor() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    internal fun formatTime(time: Int): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        return formatter.format(Date(time.toLong() - 1000 * 60 * 60))
    }

    override fun onPause() {
        super.onPause()
        looper?.removeCallbacks(loopTask())
        looper = null

        getSharedPreferences("player", MODE_PRIVATE).edit().putInt("seek", videoView.currentPosition).apply()
    }

    override fun onResume() {
        super.onResume()
        loadingContainer.visibility = View.VISIBLE
        looper = Handler(Looper.getMainLooper())
        looper?.post(loopTask())

        val seek = getSharedPreferences("player", MODE_PRIVATE).getInt("seek", 0)

        if(getVideoUri() != Uri.EMPTY){
            playedTime.text = formatTime(seek)
            videoView.seekTo(seek)
            videoView.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        looper?.removeCallbacks(loopTask())
        looper = null
        getSharedPreferences("player", MODE_PRIVATE).edit().remove("seek").apply()
    }

    private val mSessionManagerListener: SessionManagerListener<CastSession> = SessionManagerListenerImpl()

    private inner class SessionManagerListenerImpl : SessionManagerListener<CastSession> {
        override fun onSessionStarted(session: CastSession?, sessionId: String) {
            invalidateOptionsMenu()
        }

        override fun onSessionResumed(session: CastSession?, wasSuspended: Boolean) {
            invalidateOptionsMenu()
        }

        override fun onSessionEnded(session: CastSession?, error: Int) {
            finish()
        }

        override fun onSessionStarting(p0: CastSession) {

        }

        override fun onSessionStartFailed(p0: CastSession, p1: Int) {
        }

        override fun onSessionEnding(p0: CastSession) {
        }

        override fun onSessionResuming(p0: CastSession, p1: String) {
        }

        override fun onSessionResumeFailed(p0: CastSession, p1: Int) {
        }

        override fun onSessionSuspended(p0: CastSession, p1: Int) {
        }
    }
}



