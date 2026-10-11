package com.achievement.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.achievement.app.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>
    
    @Query("SELECT * FROM achievements WHERE appId = :appId")
    suspend fun getAchievementById(appId: String): AchievementEntity?
    
    @Query("SELECT * FROM achievements WHERE platinumEarned > 0")
    fun getPlatinumAchievements(): Flow<List<AchievementEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)
    
    @Query("DELETE FROM achievements")
    suspend fun deleteAllAchievements()
    
    @Query("SELECT SUM(platinumEarned) FROM achievements")
    fun getTotalPlatinum(): Flow<Int>
    
    @Query("SELECT SUM(goldEarned) FROM achievements")
    fun getTotalGold(): Flow<Int>
    
    @Query("SELECT SUM(silverEarned) FROM achievements")
    fun getTotalSilver(): Flow<Int>
    
    @Query("SELECT SUM(bronzeEarned) FROM achievements")
    fun getTotalBronze(): Flow<Int>
}
