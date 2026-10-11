package com.achievement.app.data.local.entity

import androidx.room.Entity

@Entity(tableName = "trophies", primaryKeys = ["appId", "trophyId"])
data class TrophyEntity(
    val appId: String,
    val trophyId: Int,
    val name: String,
    val detail: String,
    val type: String,
    val iconUrl: String?,
    val isHidden: Boolean,
    val earnedRate: Double,
    val groupId: String,
    val isEarned: Boolean,
    val earnedDateTime: String?,
    val rarity: String
)
