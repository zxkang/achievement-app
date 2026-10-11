package com.achievement.app.data.remote.interceptor

import com.achievement.app.data.repository.AuthRepository
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authRepository: Lazy<AuthRepository>
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val url = originalRequest.url.toString()
        if (url.contains("/oauth/")) {
            return chain.proceed(originalRequest)
        }
        
        val accessToken = runBlocking {
            val repo = authRepository.get()
            if (repo.shouldRefreshToken()) {
                repo.refreshAccessToken()
            }
            repo.getAccessToken()
        } ?: return chain.proceed(originalRequest)
        
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
}
