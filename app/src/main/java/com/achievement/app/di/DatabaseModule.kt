package com.achievement.app.di

import android.content.Context
import androidx.room.Room
import com.achievement.app.data.local.AppDatabase
import com.achievement.app.data.local.dao.AchievementDao
import com.achievement.app.data.local.dao.GameDao
import com.achievement.app.data.local.dao.PlatinumHallDao
import com.achievement.app.data.local.dao.PlayerDao
import com.achievement.app.data.local.dao.TrophyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "achievement_database"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideGameDao(database: AppDatabase): GameDao {
        return database.gameDao()
    }

    @Provides
    @Singleton
    fun provideAchievementDao(database: AppDatabase): AchievementDao {
        return database.achievementDao()
    }

    @Provides
    @Singleton
    fun providePlayerDao(database: AppDatabase): PlayerDao {
        return database.playerDao()
    }

    @Provides
    @Singleton
    fun providePlatinumHallDao(database: AppDatabase): PlatinumHallDao {
        return database.platinumHallDao()
    }

    @Provides
    @Singleton
    fun provideTrophyDao(database: AppDatabase): TrophyDao {
        return database.trophyDao()
    }
}
