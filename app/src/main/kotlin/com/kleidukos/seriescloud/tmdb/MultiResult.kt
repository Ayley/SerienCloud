package com.kleidukos.seriescloud.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class MultiResult(
    @SerialName("poster_path")
    val poster: String? = null,
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("overview")
    val description: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("genre_ids")
    val genreIds: Array<Int>? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("media_type")
    val mediaType: String? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("popularity")
    val popularity: String? = null,
    @SerialName("vote_count")
    val voteCount: Int? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("vote_average")
    val voteAverage: Float? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("origin_country")
    val originCountry: Array<String>? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("original_name")
    val originalName: String? = null
)
