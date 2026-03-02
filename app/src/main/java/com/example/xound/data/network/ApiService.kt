package com.example.xound.data.network

import com.example.xound.data.model.AuthRequest
import com.example.xound.data.model.AuthResponse
import com.example.xound.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/users/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}
