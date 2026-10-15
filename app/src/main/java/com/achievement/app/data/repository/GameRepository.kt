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
import com.achievement.app.domain.util.Constants
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
        // 一次性读取 games + achievements 并在内存建索引,避免逐个 game 触发 getAchievementById 的 N+1 查询
        return combine(
            gameDao.getAllGames(),
            achievementDao.getAllAchievements()
        ) { gameEntities, achievementEntities ->
            val achievementByKey = achievementEntities.associateBy { it.appId }
            gameEntities.map { gameEntity ->
                val game = GameMapper.entityToDomain(gameEntity)
                val key = game.npCommunicationId ?: game.appId
                Pair(game, achievementByKey[key]?.let { TrophyMapper.entityToDomain(it) })
            }
        }
    }

    fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements().map { entities ->
            entities.map { TrophyMapper.entityToDomain(it) }
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

            // 三个接口互不依赖,并发请求;串行时耗时是三者之和,PSN API 单次延迟 1~2s
            val (enGameList, zhGameList, trophyTitlesResponse) = coroutineScope {
                val en = async {
                    gameApi.getUserGames(
                        accountId = accountId,
                        acceptLanguage = Constants.LANG_ENGLISH,
                        authorization = authorization
                    )
                }
                val zh = async {
                    gameApi.getUserGames(
                        accountId = accountId,
                        acceptLanguage = Constants.LANG_CHINESE,
                        authorization = authorization
                    )
                }
                val trophies = async {
                    trophyApi.getUserTrophyTitles(
                        accountId = accountId,
                        authorization = authorization
                    )
                }
                Triple(en.await(), zh.await(), trophies.await())
            }

            // 以 titleId 为键建立 英文规范化名 -> 中文名 映射(Trophy API 不支持中文,只能靠英文名关联)
            val chineseNameMap = GameMapper.buildChineseNameMap(enGameList.titles, zhGameList.titles)

            val gameEntities = GameMapper.buildGameEntities(
                enTitles = enGameList.titles,
                chineseNameByNormalizedEnglish = chineseNameMap,
                trophyTitles = trophyTitlesResponse.trophyTitles
            )
            // 先清空再写入:合并去重后被丢弃的变体行不会残留,避免重复与过期数据累积
            gameDao.deleteAllGames()
            achievementDao.deleteAllAchievements()
            platinumHallDao.deleteAllPlatinumHall()
            gameDao.insertGames(gameEntities)

            val achievementEntities = TrophyMapper.dtoListToAchievementEntityList(
                trophyTitlesResponse.trophyTitles
            )
            achievementDao.insertAchievements(achievementEntities)

            val platinumHalls = trophyTitlesResponse.trophyTitles
                .mapNotNull {
                    TrophyMapper.dtoToPlatinumHallEntity(
                        it,
                        displayName = chineseNameMap[GameMapper.normalizeGameName(it.trophyTitleName)]
                    )
                }
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
            val accountId = authRepository.getAccountId()
                ?: return Result.failure(Exception("无账号"))

            // 两个接口互不依赖,并发请求
            val (titleTrophiesResponse, userTrophiesResponse) = coroutineScope {
                val title = async {
                    trophyApi.getTitleTrophies(
                        npCommunicationId = npCommunicationId,
                        trophyGroupId = "default",
                        npServiceName = npServiceName,
                        authorization = authorization
                    )
                }
                val earned = async {
                    trophyApi.getUserTrophiesEarnedForTitle(
                        accountId = accountId,
                        npCommunicationId = npCommunicationId,
                        trophyGroupId = "default",
                        npServiceName = npServiceName,
                        authorization = authorization
                    )
                }
                title.await() to earned.await()
            }

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
