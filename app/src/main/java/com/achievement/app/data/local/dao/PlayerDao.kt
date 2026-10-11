package com.achievement.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.achievement.app.data.local.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    
    @Query("SELECT * FROM player LIMIT 1")
    fun getPlayer(): Flow<PlayerEntity?>
    
    @Query("SELECT * FROM player WHERE accountId = :accountId")
    suspend fun getPlayerById(accountId: String): PlayerEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)
    
    @Query("DELETE FROM player")
    suspend fun deletePlayer()
}
