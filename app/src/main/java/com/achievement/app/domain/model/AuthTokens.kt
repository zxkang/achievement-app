package com.achievement.app.domain.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val refreshTokenExpiresIn: Long,
    val tokenType: String
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= expiresIn
    }
    
    fun shouldRefresh(): Boolean {
        val threshold = 5 * 60 * 1000L
        return System.currentTimeMillis() >= (expiresIn - threshold)
    }
}
