package com.achievement.app.domain.model

data class Player(
    val accountId: String,
    val onlineId: String,
    val avatarUrl: String?,
    val aboutMe: String = "",
    val isPlus: Boolean = false,
    val isOnline: Boolean = false,
    val currentPlatform: String? = null,
    val currentGame: String? = null,
    val trophyLevel: Int,
    val trophyProgress: Int,
    val trophyTier: Int,
    val platinumCount: Int,
    val goldCount: Int,
    val silverCount: Int,
    val bronzeCount: Int
) {
    val tierName: String
        get() = when (trophyTier) {
            in 1..3 -> "青铜"
            in 4..6 -> "白银"
            in 7..9 -> "黄金"
            10 -> "白金"
            else -> "未知"
        }
    
    val totalTrophies: Int
        get() = platinumCount + goldCount + silverCount + bronzeCount
}
