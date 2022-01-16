package com.kleidukos.seriescloud.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kleidukos.seriescloud.R
import com.kleidukos.seriescloud.backend.Episode

class EpisodeItem: ConstraintLayout {

    private val number: TextView
    private val germanTitle: TextView
    private val englishTitle: TextView

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.episode_item, this)

        number = findViewById(R.id.episode_number)
        germanTitle = findViewById(R.id.episode_german_title)
        englishTitle = findViewById(R.id.episode_english_title)
    }

    constructor(context: Context, episode: Episode): super(context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.episode_item, this)

        number = findViewById(R.id.episode_number)
        germanTitle = findViewById(R.id.episode_german_title)
        englishTitle = findViewById(R.id.episode_english_title)

        number.text = episode.number.toString()
        episode.germanTitle?.let { germanTitle.text = it }
        episode.englishTitle?.let { englishTitle.text = it }
    }

}