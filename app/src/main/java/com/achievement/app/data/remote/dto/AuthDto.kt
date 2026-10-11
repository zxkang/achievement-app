package com.achievement.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("refresh_token_expires_in") val refreshTokenExpiresIn: Long,
    @SerialName("token_type") val tokenType: String,
    @SerialName("id_token") val idToken: String? = null,
    @SerialName("scope") val scope: String? = null
)

@Serializable
data class TokenRequest(
    @SerialName("code") val code: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("redirect_uri") val redirectUri: String = "com.scee.psxandroid.scecompcall://redirect",
    @SerialName("grant_type") val grantType: String,
    @SerialName("token_format") val tokenFormat: String = "jwt"
)
