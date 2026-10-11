package com.achievement.app.data.mapper

import com.achievement.app.data.local.entity.PlayerEntity
import com.achievement.app.data.remote.dto.BasicPresenceResponse
import com.achievement.app.data.remote.dto.TrophySummaryResponse
import com.achievement.app.data.remote.dto.UserProfileResponse
import com.achievement.app.domain.model.Player
import com.achievement.app.domain.util.PsnImageUrl

object PlayerMapper {

    fun toPlayerEntity(
        profile: UserProfileResponse,
        summary: TrophySummaryResponse,
        presence: BasicPresenceResponse? = null
    ): PlayerEntity {
        val avatarUrl = PsnImageUrl.forceRaster(
            profile.avatars
                .firstOrNull { it.size == "l" }
                ?.httpsUrl
                ?: profile.avatars.firstOrNull()?.httpsUrl
        )

        val isOnline = presence?.availability == "availableToPlay" &&
            presence.primaryPlatformInfo?.onlineStatus == "online"

        val currentPlatform = if (isOnline) presence?.primaryPlatformInfo?.platform else null

        val currentGame = if (isOnline) {
            presence?.gameTitleInfoList?.firstOrNull()?.titleName
        } else null

        return PlayerEntity(
            accountId = summary.accountId,
            onlineId = profile.onlineId,
            avatarUrl = avatarUrl,
            aboutMe = profile.aboutMe,
            isPlus = profile.isPlus,
            isOnline = isOnline,
            currentPlatform = currentPlatform,
            currentGame = currentGame,
            trophyLevel = summary.trophyLevel,
            trophyProgress = summary.progress,
            trophyTier = summary.tier,
            platinumCount = summary.earnedTrophies.platinum,
            goldCount = summary.earnedTrophies.gold,
            silverCount = summary.earnedTrophies.silver,
            bronzeCount = summary.earnedTrophies.bronze,
            lastSyncAt = System.currentTimeMillis()
        )
    }

    fun entityToDomain(entity: PlayerEntity): Player {
        return Player(
            accountId = entity.accountId,
            onlineId = entity.onlineId,
            avatarUrl = entity.avatarUrl,
            aboutMe = entity.aboutMe,
            isPlus = entity.isPlus,
            isOnline = entity.isOnline,
            currentPlatform = entity.currentPlatform,
            currentGame = entity.currentGame,
            trophyLevel = entity.trophyLevel,
            trophyProgress = entity.trophyProgress,
            trophyTier = entity.trophyTier,
            platinumCount = entity.platinumCount,
            goldCount = entity.goldCount,
            silverCount = entity.silverCount,
            bronzeCount = entity.bronzeCount,
            lastSyncAt = entity.lastSyncAt
        )
    }
}
