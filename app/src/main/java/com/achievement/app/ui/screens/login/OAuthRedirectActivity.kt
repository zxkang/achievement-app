package com.achievement.app.ui.screens.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.MutableStateFlow

class OAuthRedirectActivity : ComponentActivity() {
    
    companion object {
        val authCodeFlow = MutableStateFlow<String?>(null)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val uri = intent?.data
        Log.e("OAuthRedirect", "✓ Redirect URI: $uri")
        
        if (uri != null) {
            val code = uri.getQueryParameter("code")
            Log.e("OAuthRedirect", "✓ Auth code: ${if (code.isNullOrBlank()) "null" else "success"}")
            
            if (!code.isNullOrBlank()) {
                authCodeFlow.value = code
            }
        }
        
        finish()
    }
}
