package com.achievement.app.ui.screens.login

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.browser.customtabs.CustomTabsIntent
import com.achievement.app.domain.util.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewLoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val authCode by OAuthRedirectActivity.authCodeFlow.collectAsState()

    LaunchedEffect(Unit) {
        delay(300)
        val oauthUrl = Uri.parse(
            "${Constants.PSN_LOGIN_URL}" +
            "?access_type=offline" +
            "&client_id=${Constants.CLIENT_ID}" +
            "&redirect_uri=${Constants.REDIRECT_URI}" +
            "&response_type=code" +
            "&scope=${Uri.encode(Constants.SCOPE)}"
        )
        
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        
        customTabsIntent.launchUrl(context, oauthUrl)
    }

    LaunchedEffect(authCode) {
        val code = authCode
        if (!code.isNullOrBlank()) {
            OAuthRedirectActivity.authCodeFlow.value = null
            onLoginSuccess(code)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PlayStation 登录") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("请在浏览器中完成登录")
                Text("登录成功后会自动返回应用")
            }
        }
    }
}
