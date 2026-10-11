package com.achievement.app.data.remote.api

import com.achievement.app.data.remote.dto.AuthTokenResponse
import com.achievement.app.data.remote.dto.TokenRequest
import com.achievement.app.domain.util.Constants
import retrofit2.http.*

interface AuthApi {
    
    @Headers("Accept: application/json")
    @GET("authorize")
    suspend fun getAccessCode(
        @Query("access_type") accessType: String = "offline",
        @Query("client_id") clientId: String = Constants.CLIENT_ID,
        @Query("redirect_uri") redirectUri: String = Constants.REDIRECT_URI,
        @Query("response_type") responseType: String = "code",
        @Query("scope") scope: String = Constants.SCOPE,
        @Header("Cookie") cookie: String
    ): retrofit2.Response<Unit>
    
    @FormUrlEncoded
    @POST("token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun exchangeCodeForToken(
        @Header("Authorization") authorization: String = Constants.CLIENT_AUTH,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = Constants.REDIRECT_URI,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("token_format") tokenFormat: String = "jwt"
    ): AuthTokenResponse
    
    @FormUrlEncoded
    @POST("token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun refreshToken(
        @Header("Authorization") authorization: String = Constants.CLIENT_AUTH,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): AuthTokenResponse
}
