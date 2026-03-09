package com.example.xound.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import java.net.URLEncoder

object CoverArtService {

    private val cache = mutableMapOf<String, String?>()

    suspend fun getCoverUrl(artist: String?, title: String): String? {
        val key = "${artist.orEmpty()}-$title"
        if (cache.containsKey(key)) return cache[key]

        return withContext(Dispatchers.IO) {
            try {
                val query = if (!artist.isNullOrBlank()) "$artist $title" else title
                val encoded = URLEncoder.encode(query, "UTF-8")
                val url = "https://itunes.apple.com/search?term=$encoded&media=music&limit=1"
                val response = URL(url).readText()
                val json = JSONArray(org.json.JSONObject(response).getString("results"))
                val coverUrl = if (json.length() > 0) {
                    val artwork = json.getJSONObject(0).optString("artworkUrl100", "")
                    // Get higher resolution (600x600)
                    artwork.replace("100x100", "600x600").ifBlank { null }
                } else null
                cache[key] = coverUrl
                coverUrl
            } catch (_: Exception) {
                cache[key] = null
                null
            }
        }
    }
}
