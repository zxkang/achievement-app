package com.achievement.app.domain.util

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * PSN 图片请求的统一入口,保证预取和展示使用同一个尺寸。
 *
 * Coil 以 URL 命中内存缓存后还会校验位图尺寸,缓存的位图比请求的小就判定未命中并重新解码,
 * 所以预取必须用与 AsyncImage 相同的尺寸,否则预取白做。
 */
object PsnImages {
    const val TROPHY_ICON_PX = 144
    const val GAME_HEADER_PX = 800
    const val GAME_THUMB_PX = 220
    const val AVATAR_PX = 240

    // psnobj 只支持 HTTP/1.1(无法多路复用),单张首字节在 0.5~7s 之间抖动,体积反而不是瓶颈,
    // 所以并发并非越高越好:实测 16 并发时单张 p50 从 1.3s 恶化到 6.6s,因为请求在连接池里互相排队。
    const val TROPHY_PARALLELISM = 8

    // 封面是 800px 大图,并发再高会抢光首屏渲染要用的连接和带宽(实测预取并发 24 时
    // MainActivity 首帧从 2.5s 涨到 78s)。
    const val COVER_PARALLELISM = 4

    /** [url] 需为 PsnImageUrl.sized 的结果,[sizePx] 与其 w= 参数一致。 */
    fun request(context: Context, url: String, sizePx: Int): ImageRequest =
        ImageRequest.Builder(context.applicationContext).data(url).size(sizePx).build()

    /** 按列表顺序限流预取 [urls](已 sized 过的),等待每张完成以控制并发。 */
    fun prefetch(
        scope: CoroutineScope,
        context: Context,
        urls: List<String>,
        sizePx: Int,
        parallelism: Int = TROPHY_PARALLELISM
    ) {
        if (urls.isEmpty()) return
        val appContext = context.applicationContext
        val loader = appContext.imageLoader
        scope.launch {
            val permits = Semaphore(parallelism)
            coroutineScope {
                urls.forEach { url ->
                    launch {
                        permits.withPermit { loader.execute(request(appContext, url, sizePx)) }
                    }
                }
            }
        }
    }
}
