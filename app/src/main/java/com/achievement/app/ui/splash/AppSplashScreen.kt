package com.achievement.app.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.achievement.app.R
import com.achievement.app.ui.theme.PSNBackground
import kotlinx.coroutines.delay

/**
 * 应用启动品牌页:纯静态深色底 + 白金奖杯,无文字、无动画。
 * 奖杯尺寸与系统 SplashScreen 图标保持一致,过渡无大小跳变。
 */
@Composable
fun AppSplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(900)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PSNBackground),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.trophy_platinum),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = 84.dp, height = 158.dp)
        )
    }
}
