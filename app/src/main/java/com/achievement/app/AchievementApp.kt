package com.achievement.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class AchievementApp : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .crossfade(true)
        .memoryCache {
            MemoryCache.Builder(this).maxSizePercent(0.25).build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve("image_cache"))
                .maxSizeBytes(512L * 1024 * 1024)
                .build()
        }
        // PSN 图片 CDN 只支持 HTTP/1.1 且首字节延迟波动大(0.5s~7s)、偶发连接重置,
        // OkHttp 默认每主机仅 5 个并行请求,列表 20+ 张图只能排队。这里放宽并发并保活连接,
        // 上限要高于预取并发,否则可见项的请求会排在预取后面。
        .okHttpClient {
            OkHttpClient.Builder()
                .dispatcher(Dispatcher().apply {
                    maxRequests = 96
                    maxRequestsPerHost = 32
                })
                .connectionPool(ConnectionPool(32, 5, TimeUnit.MINUTES))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }
        .build()
}
