package com.example.xound.data.model

data class EventResponse(
    val id: Long = 0,
    val title: String = "",
    val eventDate: String? = null,
    val venue: String? = null,
    val published: Boolean = false,
    val shareCode: String? = null,
    val userId: Long? = null,
    val status: Boolean = true,
    val createdAt: String? = null
)

data class CreateEventRequest(
    val title: String,
    val eventDate: String?,
    val venue: String?
)

data class SetlistSongResponse(
    val id: Long = 0,
    val eventId: Long = 0,
    val songId: Long = 0,
    val position: Int = 0,
    // Nested song (if API embeds it)
    val song: SongResponse? = null,
    // Flat song fields from backend JOIN
    val songTitle: String? = null,
    val songArtist: String? = null,
    val songTone: String? = null,
    val songContent: String? = null,
    val songLyrics: String? = null,
    val songNotes: String? = null,
    val songBpm: Int? = null,
    val songTimeSignature: String? = null
) {
    /** Resolves the song from either nested object or flat fields */
    fun resolvedSong(): SongResponse? {
        if (song != null) return song
        if (songTitle == null) return null
        return SongResponse(
            id = songId,
            title = songTitle,
            artist = songArtist,
            tone = songTone,
            content = songContent,
            lyrics = songLyrics ?: songContent,
            notes = songNotes,
            bpm = songBpm,
            timeSignature = songTimeSignature
        )
    }
}
