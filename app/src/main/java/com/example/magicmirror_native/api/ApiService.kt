// api/ApiService.kt
package com.example.magicmirror_native.api

import com.example.magicmirror_native.models.LoginRequest
import com.example.magicmirror_native.models.LoginResponse
import com.example.magicmirror_native.models.Mirror
import com.example.magicmirror_native.models.User
import retrofit2.Call
import retrofit2.http.*
import com.example.magicmirror_native.models.MirrorStateUpdate

interface ApiService {
    @POST("users/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("users/me")
    fun getCurrentUser(@Header("Authorization") token: String): Call<Map<String, User>>

    @GET("mirrors")
    fun getMirrors(@Header("Authorization") token: String): Call<List<Mirror>>

    @GET("mirrors/{id}")
    fun getMirrorById(
        @Header("Authorization") token: String,
        @Path("id") mirrorId: Int
    ): Call<Mirror>

    @PATCH("mirrors/{id}/state")
    fun updateMirrorState(
        @Header("Authorization") token: String,
        @Path("id") mirrorId: Int,
        @Body updateData: MirrorStateUpdate
    ): Call<Mirror>
}