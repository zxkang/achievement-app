package com.achievement.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val appId: String,
    val name: String,
    val platform: String,
    val playtimeMinutes: Long,
    val headerImage: String?,
    val lastPlayed: String?,
    val npCommunicationId: String? = null,
    val npServiceName: String? = null
)
