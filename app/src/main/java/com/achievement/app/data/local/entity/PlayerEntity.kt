package com.achievement.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey val accountId: String,
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
    val bronzeCount: Int,
    val lastSyncAt: Long = 0L
)
