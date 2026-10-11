package com.achievement.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.achievement.app.data.local.entity.PlatinumHallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatinumHallDao {
    
    @Query("SELECT * FROM platinum_hall ORDER BY platinumEarnedDate DESC")
    fun getAllPlatinumHall(): Flow<List<PlatinumHallEntity>>
    
    @Query("SELECT * FROM platinum_hall WHERE year = :year ORDER BY platinumEarnedDate DESC")
    fun getPlatinumHallByYear(year: Int): Flow<List<PlatinumHallEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatinumHall(platinumHalls: List<PlatinumHallEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatinumHall(platinumHall: PlatinumHallEntity)
    
    @Query("DELETE FROM platinum_hall")
    suspend fun deleteAllPlatinumHall()
    
    @Query("SELECT COUNT(*) FROM platinum_hall")
    fun getPlatinumCount(): Flow<Int>
}
