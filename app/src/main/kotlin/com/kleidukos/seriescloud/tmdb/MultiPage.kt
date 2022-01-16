package com.kleidukos.seriescloud.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MultiPage(
    @SerialName("page")
    val page: Int? = null,
    @SerialName("results")
    val results: List<MultiResult>,
    @SerialName("total_results")
    val totalResults: Int? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null
)
