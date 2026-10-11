package com.achievement.app.data.remote.api

import com.achievement.app.data.remote.dto.BasicPresenceResponse
import com.achievement.app.data.remote.dto.UserProfileResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    
    @GET("/api/userProfile/v1/internal/users/{accountId}/profiles")
    suspend fun getUserProfile(
        @Path("accountId") accountId: String,
        @Header("Authorization") authorization: String
    ): UserProfileResponse
    
    @GET("/api/userProfile/v1/internal/users/{accountId}/basicPresences")
    suspend fun getBasicPresence(
        @Path("accountId") accountId: String,
        @Query("type") type: String = "primary",
        @Header("Authorization") authorization: String
    ): BasicPresenceResponse
}
