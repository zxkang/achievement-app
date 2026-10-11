package com.achievement.app.data.remote.api

import com.achievement.app.data.remote.dto.GameListResponse
import com.achievement.app.domain.util.Constants
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {
    
    @GET("/api/gamelist/v2/users/{accountId}/titles")
    suspend fun getUserGames(
        @Path("accountId") accountId: String,
        @Query("limit") limit: Int = Constants.GAME_LIST_LIMIT,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ): GameListResponse
}
