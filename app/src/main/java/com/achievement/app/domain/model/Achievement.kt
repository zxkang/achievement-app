package com.achievement.app.domain.model

data class Achievement(
    val appId: String,
    val achievementTotal: Int,
    val achievementUnlocked: Int,
    val platinumTotal: Int,
    val platinumEarned: Int,
    val goldTotal: Int,
    val goldEarned: Int,
    val silverTotal: Int,
    val silverEarned: Int,
    val bronzeTotal: Int,
    val bronzeEarned: Int
) {
    val progressPercent: Int
        get() = if (achievementTotal > 0) (achievementUnlocked * 100 / achievementTotal) else 0
    
    val isPlatinum: Boolean
        get() = platinumEarned > 0
}
