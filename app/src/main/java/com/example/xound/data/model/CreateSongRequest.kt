package com.example.xound.data.model

data class CreateSongRequest(
    val title: String,
    val artist: String?,
    val tone: String?,
    val bpm: Int?,
    val timeSignature: String?,
    val lyrics: String?,
    val content: String? = null,
    val notes: String? = null
)

data class LyricsResponse(
    val lyrics: String? = null
)

data class ChordSearchResult(
    val title: String? = null,
    val artist: String? = null,
    val url: String? = null
)

data class ChordFetchResult(
    val content: String? = null
)
