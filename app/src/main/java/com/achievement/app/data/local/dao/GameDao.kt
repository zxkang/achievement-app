package com.achievement.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.achievement.app.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    
    @Query("SELECT * FROM games ORDER BY lastPlayed DESC")
    fun getAllGames(): Flow<List<GameEntity>>
    
    @Query("SELECT * FROM games WHERE appId = :appId")
    suspend fun getGameById(appId: String): GameEntity?
    
    @Query("SELECT * FROM games WHERE name LIKE '%' || :query || '%'")
    fun searchGames(query: String): Flow<List<GameEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)
    
    @Query("DELETE FROM games")
    suspend fun deleteAllGames()
    
    @Query("SELECT COUNT(*) FROM games")
    fun getGameCount(): Flow<Int>
}
