package com.achievement.app.data.repository

import android.util.Base64
import com.achievement.app.data.local.TokenDataStore
import com.achievement.app.data.remote.api.AuthApi
import com.achievement.app.domain.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore
) {
    suspend fun loginWithNpsso(npssoToken: String): Result<AuthTokens> {
        return try {
            val cookie = "npsso=$npssoToken"
            val response = authApi.getAccessCode(cookie = cookie)
            
            val location = response.headers()["location"]
            if (location == null || !location.contains("?code=")) {
                return Result.failure(Exception("Invalid NPSSO token"))
            }
            
            val code = location.substringAfter("?code=").substringBefore("&")
            exchangeCodeAndSave(code)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun loginWithAuthCode(code: String): Result<AuthTokens> {
        return try {
            exchangeCodeAndSave(code)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun exchangeCodeAndSave(code: String): Result<AuthTokens> {
        val tokenResponse = authApi.exchangeCodeForToken(code = code)
        
        val tokens = AuthTokens(
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            expiresIn = System.currentTimeMillis() + (tokenResponse.expiresIn * 1000),
            refreshTokenExpiresIn = System.currentTimeMillis() + (tokenResponse.refreshTokenExpiresIn * 1000),
            tokenType = tokenResponse.tokenType
        )
        
        tokenDataStore.saveTokens(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresIn = tokenResponse.expiresIn
        )
        
        tokenResponse.idToken?.let { idToken ->
            parseAccountIdFromIdToken(idToken)?.let { accountId ->
                tokenDataStore.saveAccountInfo(accountId, "")
            }
        }
        
        return Result.success(tokens)
    }
    
    private fun parseAccountIdFromIdToken(idToken: String): String? {
        return try {
            val parts = idToken.split(".")
            if (parts.size != 3) return null
            
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            val json = JSONObject(payload)
            json.optString("sub", null)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun refreshAccessToken(): Result<AuthTokens> {
        return try {
            val refreshToken = tokenDataStore.getRefreshToken()
                ?: return Result.failure(Exception("No refresh token"))
            
            val tokenResponse = authApi.refreshToken(refreshToken = refreshToken)
            
            val tokens = AuthTokens(
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken,
                expiresIn = System.currentTimeMillis() + (tokenResponse.expiresIn * 1000),
                refreshTokenExpiresIn = System.currentTimeMillis() + (tokenResponse.refreshTokenExpiresIn * 1000),
                tokenType = tokenResponse.tokenType
            )
            
            tokenDataStore.saveTokens(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken,
                expiresIn = tokenResponse.expiresIn
            )
            
            Result.success(tokens)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAccessToken(): String? = tokenDataStore.getAccessToken()
    
    suspend fun getAccountId(): String? = tokenDataStore.getAccountId()
    
    suspend fun saveAccountInfo(accountId: String, onlineId: String) {
        tokenDataStore.saveAccountInfo(accountId, onlineId)
    }
    
    fun isLoggedIn(): Flow<Boolean> = tokenDataStore.isLoggedIn()
    
    suspend fun logout() {
        tokenDataStore.clear()
    }
    
    suspend fun shouldRefreshToken(): Boolean {
        val expiresAt = tokenDataStore.getTokenExpiresAt() ?: return false
        val threshold = 5 * 60 * 1000L
        return System.currentTimeMillis() >= (expiresAt - threshold)
    }
}
