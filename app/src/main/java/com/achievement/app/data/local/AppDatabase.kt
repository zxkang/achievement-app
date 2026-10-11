package com.achievement.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.achievement.app.data.local.dao.AchievementDao
import com.achievement.app.data.local.dao.GameDao
import com.achievement.app.data.local.dao.PlatinumHallDao
import com.achievement.app.data.local.dao.PlayerDao
import com.achievement.app.data.local.dao.TrophyDao
import com.achievement.app.data.local.entity.AchievementEntity
import com.achievement.app.data.local.entity.GameEntity
import com.achievement.app.data.local.entity.PlatinumHallEntity
import com.achievement.app.data.local.entity.PlayerEntity
import com.achievement.app.data.local.entity.TrophyEntity

@Database(
    entities = [
        GameEntity::class,
        AchievementEntity::class,
        PlayerEntity::class,
        PlatinumHallEntity::class,
        TrophyEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun achievementDao(): AchievementDao
    abstract fun playerDao(): PlayerDao
    abstract fun platinumHallDao(): PlatinumHallDao
    abstract fun trophyDao(): TrophyDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE games ADD COLUMN npCommunicationId TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE games ADD COLUMN npServiceName TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE player ADD COLUMN aboutMe TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE player ADD COLUMN isPlus INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE player ADD COLUMN isOnline INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE player ADD COLUMN currentPlatform TEXT DEFAULT NULL")
                database.execSQL("ALTER TABLE player ADD COLUMN currentGame TEXT DEFAULT NULL")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS trophies (
                        appId TEXT NOT NULL,
                        trophyId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        detail TEXT NOT NULL,
                        type TEXT NOT NULL,
                        iconUrl TEXT DEFAULT NULL,
                        isHidden INTEGER NOT NULL DEFAULT 0,
                        earnedRate REAL NOT NULL DEFAULT 0.0,
                        groupId TEXT NOT NULL DEFAULT 'default',
                        isEarned INTEGER NOT NULL DEFAULT 0,
                        earnedDateTime TEXT DEFAULT NULL,
                        rarity TEXT NOT NULL DEFAULT '',
                        PRIMARY KEY(appId, trophyId)
                    )
                """.trimIndent())
            }
        }
    }
}
