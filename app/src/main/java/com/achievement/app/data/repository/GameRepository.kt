package com.achievement.app.data.repository

import com.achievement.app.data.local.dao.AchievementDao
import com.achievement.app.data.local.dao.GameDao
import com.achievement.app.data.local.dao.PlatinumHallDao
import com.achievement.app.data.local.dao.TrophyDao
import com.achievement.app.data.mapper.GameMapper
import com.achievement.app.data.mapper.TrophyDetailMapper
import com.achievement.app.data.mapper.TrophyMapper
import com.achievement.app.data.remote.api.GameApi
import com.achievement.app.data.remote.api.TrophyApi
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.model.PlatinumHall
import com.achievement.app.domain.model.Trophy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameApi: GameApi,
    private val trophyApi: TrophyApi,
    private val gameDao: GameDao,
    private val achievementDao: AchievementDao,
    private val platinumHallDao: PlatinumHallDao,
    private val trophyDao: TrophyDao,
    private val authRepository: AuthRepository
) {
    fun getAllGames(): Flow<List<Game>> {
        return gameDao.getAllGames().map { entities ->
            GameMapper.entityListToDomainList(entities)
        }
    }

    fun getGamesWithAchievements(): Flow<List<Pair<Game, Achievement?>>> {
        return gameDao.getAllGames().map { gameEntities ->
            val games = GameMapper.entityListToDomainList(gameEntities)
            games.map { game ->
                val achievementKey = game.npCommunicationId ?: game.appId
                val achievement = achievementDao.getAchievementById(achievementKey)
                Pair(game, achievement?.let { TrophyMapper.entityToDomain(it) })
            }
        }
    }

    fun getPlatinumHall(): Flow<List<PlatinumHall>> {
        return platinumHallDao.getAllPlatinumHall().map { entities ->
            TrophyMapper.platinumEntityListToDomainList(entities)
        }
    }

    fun getTrophiesForGame(appId: String): Flow<List<Trophy>> {
        return trophyDao.getTrophiesForGame(appId).map { entities ->
            TrophyDetailMapper.entityListToDomainList(entities)
        }
    }

    suspend fun syncGamesFromApi(): Result<Unit> {
        return try {
            val accessToken = authRepository.getAccessToken()
                ?: return Result.failure(Exception("未登录"))
            val accountId = authRepository.getAccountId()
                ?: return Result.failure(Exception("无账号信息"))

            val authorization = "Bearer $accessToken"

            val gameListResponse = gameApi.getUserGames(
                accountId = accountId,
                authorization = authorization
            )

            val trophyTitlesResponse = trophyApi.getUserTrophyTitles(
                accountId = accountId,
                authorization = authorization
            )

            var gameEntities = GameMapper.dtoListToEntityList(gameListResponse.titles)
            gameEntities = GameMapper.enrichGameEntitiesWithTrophyData(
                gameEntities,
                trophyTitlesResponse.trophyTitles
            )
            gameDao.insertGames(gameEntities)

            val achievementEntities = TrophyMapper.dtoListToAchievementEntityList(
                trophyTitlesResponse.trophyTitles
            )
            achievementDao.insertAchievements(achievementEntities)

            val platinumHalls = trophyTitlesResponse.trophyTitles
                .mapNotNull { TrophyMapper.dtoToPlatinumHallEntity(it) }
            platinumHallDao.insertPlatinumHall(platinumHalls)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncTrophiesForGame(npCommunicationId: String, appId: String, npServiceName: String = "trophy"): Result<Unit> {
        return try {
            val accessToken = authRepository.getAccessToken()
                ?: return Result.failure(Exception("未登录"))

            val authorization = "Bearer $accessToken"

            val titleTrophiesResponse = trophyApi.getTitleTrophies(
                npCommunicationId = npCommunicationId,
                trophyGroupId = "default",
                npServiceName = npServiceName,
                authorization = authorization
            )

            val userTrophiesResponse = trophyApi.getUserTrophiesEarnedForTitle(
                accountId = authRepository.getAccountId() ?: return Result.failure(Exception("无账号")),
                npCommunicationId = npCommunicationId,
                trophyGroupId = "default",
                npServiceName = npServiceName,
                authorization = authorization
            )

            val trophyEntities = TrophyDetailMapper.mergeTrophies(
                titleTrophies = titleTrophiesResponse.trophies,
                userTrophies = userTrophiesResponse.trophies,
                appId = appId,
                groupId = "default"
            )

            trophyDao.deleteTrophiesForGame(appId)
            trophyDao.insertTrophies(trophyEntities)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameById(appId: String): Game? {
        return gameDao.getGameById(appId)?.let { GameMapper.entityToDomain(it) }
    }

    suspend fun getAchievementById(appId: String): Achievement? {
        return achievementDao.getAchievementById(appId)?.let { TrophyMapper.entityToDomain(it) }
    }

    fun searchGames(query: String): Flow<List<Game>> {
        return gameDao.searchGames(query).map { entities ->
            GameMapper.entityListToDomainList(entities)
        }
    }

    suspend fun clearLocalData() {
        gameDao.deleteAllGames()
        achievementDao.deleteAllAchievements()
        platinumHallDao.deleteAllPlatinumHall()
        trophyDao.deleteAllTrophies()
    }
}
