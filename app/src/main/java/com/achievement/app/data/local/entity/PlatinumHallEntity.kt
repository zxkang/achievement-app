package com.achievement.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platinum_hall")
data class PlatinumHallEntity(
    @PrimaryKey val appId: String,
    val gameName: String,
    val platinumEarnedDate: String,
    val year: Int
)
