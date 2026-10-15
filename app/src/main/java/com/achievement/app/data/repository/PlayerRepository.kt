package com.achievement.app.data.repository

import com.achievement.app.data.local.dao.PlayerDao
import com.achievement.app.data.mapper.PlayerMapper
import com.achievement.app.data.remote.api.TrophyApi
import com.achievement.app.data.remote.api.UserApi
import com.achievement.app.domain.model.Player
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val userApi: UserApi,
    private val trophyApi: TrophyApi,
    private val playerDao: PlayerDao,
    private val authRepository: AuthRepository
) {
    fun getPlayer(): Flow<Player?> {
        return playerDao.getPlayer().map { entity ->
            entity?.let { PlayerMapper.entityToDomain(it) }
        }
    }

    private val syncMutex = Mutex()

    suspend fun syncPlayerFromApi(): Result<Player> {
        // 同一次下拉刷新可能同时触发两处同步(GameViewModel/ProfileViewModel),
        // 互斥合并为一次网络往返,另一个调用拿锁后直接读已写入的本地数据。
        return syncMutex.withLock {
            try {
                val accessToken = authRepository.getAccessToken()
                    ?: return@withLock Result.failure(Exception("未登录"))
                val accountId = authRepository.getAccountId()
                    ?: return@withLock Result.failure(Exception("无账号信息"))

                val authorization = "Bearer $accessToken"

                // 资料与奖杯汇总互不依赖,并发请求
                val (profile, summary) = coroutineScope {
                    val profileTask = async {
                        userApi.getUserProfile(
                            accountId = accountId,
                            authorization = authorization
                        )
                    }
                    val summaryTask = async {
                        trophyApi.getUserTrophySummary(
                            accountId = accountId,
                            authorization = authorization
                        )
                    }
                    profileTask.await() to summaryTask.await()
                }

                val presence = try {
                    userApi.getBasicPresence(
                        accountId = accountId,
                        authorization = authorization
                    )
                } catch (e: Exception) {
                    null
                }

                val playerEntity = PlayerMapper.toPlayerEntity(profile, summary, presence)
                playerDao.insertPlayer(playerEntity)

                authRepository.saveAccountInfo(accountId, profile.onlineId)

                Result.success(PlayerMapper.entityToDomain(playerEntity))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun syncPresence(): Result<Unit> {
        return try {
            val accessToken = authRepository.getAccessToken()
                ?: return Result.failure(Exception("未登录"))
            val accountId = authRepository.getAccountId()
                ?: return Result.failure(Exception("无账号信息"))

            val authorization = "Bearer $accessToken"

            val presence = userApi.getBasicPresence(
                accountId = accountId,
                authorization = authorization
            )

            val currentEntity = playerDao.getPlayerById(accountId)
            if (currentEntity != null) {
                val isOnline = presence.availability == "availableToPlay" &&
                    presence.primaryPlatformInfo?.onlineStatus == "online"
                val currentPlatform = if (isOnline) presence.primaryPlatformInfo?.platform else null
                val currentGame = if (isOnline) {
                    presence.gameTitleInfoList.firstOrNull()?.titleName
                } else null

                playerDao.insertPlayer(
                    currentEntity.copy(
                        isOnline = isOnline,
                        currentPlatform = currentPlatform,
                        currentGame = currentGame
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlayerById(accountId: String): Player? {
        return playerDao.getPlayerById(accountId)?.let { PlayerMapper.entityToDomain(it) }
    }

    suspend fun clearLocalData() {
        playerDao.deletePlayer()
    }
}
