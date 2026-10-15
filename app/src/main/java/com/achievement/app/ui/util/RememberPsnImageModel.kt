package com.achievement.app.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.achievement.app.domain.util.PsnImageUrl
import com.achievement.app.domain.util.PsnImages

/**
 * 构造与预取端同尺寸的 Coil 请求,这样图标能直接命中内存缓存,不必重新下载或解码。
 * 返回 null 表示没有可用的图片地址。
 */
@Composable
fun rememberPsnImageModel(url: String?, sizePx: Int): ImageRequest? {
    val context = LocalContext.current
    return remember(context, url) {
        PsnImageUrl.sized(url, sizePx)?.let { PsnImages.request(context, it, sizePx) }
    }
}
