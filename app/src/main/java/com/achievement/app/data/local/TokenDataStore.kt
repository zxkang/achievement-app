package com.achievement.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.achievement.app.domain.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val accessTokenKey = stringPreferencesKey(Constants.KEY_ACCESS_TOKEN)
    private val refreshTokenKey = stringPreferencesKey(Constants.KEY_REFRESH_TOKEN)
    private val tokenExpiresAtKey = longPreferencesKey(Constants.KEY_TOKEN_EXPIRES_AT)
    private val accountIdKey = stringPreferencesKey(Constants.KEY_ACCOUNT_ID)
    private val onlineIdKey = stringPreferencesKey(Constants.KEY_ONLINE_ID)
    
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000)
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
            preferences[tokenExpiresAtKey] = expiresAt
        }
    }
    
    suspend fun saveAccountInfo(accountId: String, onlineId: String) {
        context.dataStore.edit { preferences ->
            preferences[accountIdKey] = accountId
            preferences[onlineIdKey] = onlineId
        }
    }
    
    val accessToken: Flow<String?> = context.dataStore.data.map { it[accessTokenKey] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[refreshTokenKey] }
    val tokenExpiresAt: Flow<Long?> = context.dataStore.data.map { it[tokenExpiresAtKey] }
    val accountId: Flow<String?> = context.dataStore.data.map { it[accountIdKey] }
    val onlineId: Flow<String?> = context.dataStore.data.map { it[onlineIdKey] }
    
    suspend fun getAccessToken(): String? = context.dataStore.data.first()[accessTokenKey]
    suspend fun getRefreshToken(): String? = context.dataStore.data.first()[refreshTokenKey]
    suspend fun getTokenExpiresAt(): Long? = context.dataStore.data.first()[tokenExpiresAtKey]
    suspend fun getAccountId(): String? = context.dataStore.data.first()[accountIdKey]
    suspend fun getOnlineId(): String? = context.dataStore.data.first()[onlineIdKey]
    
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
    
    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map { 
        it[accessTokenKey] != null 
    }
}
