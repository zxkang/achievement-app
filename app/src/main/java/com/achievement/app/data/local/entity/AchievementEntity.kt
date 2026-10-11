package com.achievement.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val appId: String,
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
)
