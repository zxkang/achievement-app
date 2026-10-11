package com.achievement.app.domain.util

import java.util.concurrent.ConcurrentHashMap

/**
 * 全局同步节流:记录各类数据上次同步的时间戳,避免每次进入页面都自动刷新。
 * 放在单例而非 ViewModel 字段,保证 TAB 切换导致 ViewModel 重建时节流依然生效。
 */
object SyncThrottle {
    const val KEY_GAMES = "games"
    const val KEY_PLAYER = "player"
    const val KEY_PRESENCE = "presence"

    const val GAMES_INTERVAL_MS = 10 * 60 * 1000L
    const val PLAYER_INTERVAL_MS = 10 * 60 * 1000L
    const val PRESENCE_INTERVAL_MS = 2 * 60 * 1000L

    private val lastRunAt = ConcurrentHashMap<String, Long>()

    fun elapsedSince(key: String): Long {
        val prev = lastRunAt[key] ?: return Long.MAX_VALUE
        return System.currentTimeMillis() - prev
    }

    fun isStale(key: String, intervalMs: Long): Boolean =
        elapsedSince(key) >= intervalMs

    fun mark(key: String) {
        lastRunAt[key] = System.currentTimeMillis()
    }

    fun reset() {
        lastRunAt.clear()
    }
}
