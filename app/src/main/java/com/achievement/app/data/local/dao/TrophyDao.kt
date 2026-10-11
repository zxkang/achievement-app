package com.achievement.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.achievement.app.data.local.entity.TrophyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrophyDao {

    @Query("SELECT * FROM trophies WHERE appId = :appId ORDER BY CASE type WHEN 'platinum' THEN 0 WHEN 'gold' THEN 1 WHEN 'silver' THEN 2 WHEN 'bronze' THEN 3 ELSE 4 END, trophyId")
    fun getTrophiesForGame(appId: String): Flow<List<TrophyEntity>>

    @Query("SELECT * FROM trophies WHERE appId = :appId ORDER BY CASE type WHEN 'platinum' THEN 0 WHEN 'gold' THEN 1 WHEN 'silver' THEN 2 WHEN 'bronze' THEN 3 ELSE 4 END, trophyId")
    suspend fun getTrophiesForGameOnce(appId: String): List<TrophyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrophies(trophies: List<TrophyEntity>)

    @Query("DELETE FROM trophies WHERE appId = :appId")
    suspend fun deleteTrophiesForGame(appId: String)

    @Query("DELETE FROM trophies")
    suspend fun deleteAllTrophies()
}
