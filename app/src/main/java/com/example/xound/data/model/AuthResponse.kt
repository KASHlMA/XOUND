package com.example.xound.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")       val token: String? = null,
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("message")     val message: String? = null,
    @SerializedName("email")       val email: String? = null
) {
    // Devuelve el token independientemente del nombre de campo que use el backend
    fun resolveToken(): String = token ?: accessToken ?: ""
}
