package com.achievement.app.data.mapper

import com.achievement.app.data.local.entity.TrophyEntity
import com.achievement.app.data.remote.dto.TrophyDetailDto
import com.achievement.app.data.remote.dto.UserEarnedTrophyDto
import com.achievement.app.domain.model.Trophy

object TrophyDetailMapper {

    fun mergeTrophies(
        titleTrophies: List<TrophyDetailDto>,
        userTrophies: List<UserEarnedTrophyDto>,
        appId: String,
        groupId: String
    ): List<TrophyEntity> {
        val userTrophyMap = userTrophies.associateBy { it.trophyId }

        return titleTrophies.map { titleTrophy ->
            val userTrophy = userTrophyMap[titleTrophy.trophyId]
            val earnedRateStr = userTrophy?.trophyEarnedRate ?: titleTrophy.trophyEarnedRate
            val earnedRate = earnedRateStr.toDoubleOrNull() ?: 0.0
            val rarity = userTrophy?.trophyRare?.let { rareInt ->
                when (rareInt) {
                    3 -> "ultraRare"
                    2 -> "veryRare"
                    1 -> "rare"
                    else -> "common"
                }
            } ?: ""
            TrophyEntity(
                appId = appId,
                trophyId = titleTrophy.trophyId,
                name = titleTrophy.trophyName,
                detail = titleTrophy.trophyDetail,
                type = titleTrophy.trophyType,
                iconUrl = titleTrophy.trophyIconUrl,
                isHidden = titleTrophy.trophyHidden,
                earnedRate = earnedRate,
                groupId = groupId,
                isEarned = userTrophy?.earned ?: false,
                earnedDateTime = userTrophy?.earnedDateTime,
                rarity = rarity
            )
        }
    }

    fun entityToDomain(entity: TrophyEntity): Trophy {
        return Trophy(
            trophyId = entity.trophyId,
            appId = entity.appId,
            name = entity.name,
            detail = entity.detail,
            type = entity.type,
            iconUrl = entity.iconUrl,
            isHidden = entity.isHidden,
            earnedRate = entity.earnedRate,
            groupId = entity.groupId,
            isEarned = entity.isEarned,
            earnedDateTime = entity.earnedDateTime,
            rarity = entity.rarity
        )
    }

    fun entityListToDomainList(entities: List<TrophyEntity>): List<Trophy> {
        return entities.map { entityToDomain(it) }
    }
}
