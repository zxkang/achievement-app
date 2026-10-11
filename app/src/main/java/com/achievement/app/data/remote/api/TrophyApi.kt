package com.achievement.app.data.remote.api

import com.achievement.app.data.remote.dto.TitleTrophiesResponse
import com.achievement.app.data.remote.dto.TrophySummaryResponse
import com.achievement.app.data.remote.dto.TrophyTitlesResponse
import com.achievement.app.data.remote.dto.UserEarnedTrophiesResponse
import com.achievement.app.domain.util.Constants
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TrophyApi {
    
    @GET("/api/trophy/v1/users/{accountId}/trophyTitles")
    suspend fun getUserTrophyTitles(
        @Path("accountId") accountId: String,
        @Query("limit") limit: Int = Constants.TROPHY_LIST_LIMIT,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ): TrophyTitlesResponse
    
    @GET("/api/trophy/v1/users/{accountId}/trophySummary")
    suspend fun getUserTrophySummary(
        @Path("accountId") accountId: String,
        @Header("Authorization") authorization: String
    ): TrophySummaryResponse
    
    @GET("/api/trophy/v1/npCommunicationIds/{npCommunicationId}/trophyGroups/{trophyGroupId}/trophies")
    suspend fun getTitleTrophies(
        @Path("npCommunicationId") npCommunicationId: String,
        @Path("trophyGroupId") trophyGroupId: String = "default",
        @Query("npServiceName") npServiceName: String = "trophy",
        @Header("Authorization") authorization: String
    ): TitleTrophiesResponse
    
    @GET("/api/trophy/v1/users/{accountId}/npCommunicationIds/{npCommunicationId}/trophyGroups/{trophyGroupId}/trophies")
    suspend fun getUserTrophiesEarnedForTitle(
        @Path("accountId") accountId: String,
        @Path("npCommunicationId") npCommunicationId: String,
        @Path("trophyGroupId") trophyGroupId: String = "default",
        @Query("npServiceName") npServiceName: String = "trophy",
        @Header("Authorization") authorization: String
    ): UserEarnedTrophiesResponse
}
