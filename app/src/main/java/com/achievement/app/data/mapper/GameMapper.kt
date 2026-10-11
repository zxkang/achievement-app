package com.achievement.app.data.mapper

import com.achievement.app.data.local.entity.GameEntity
import com.achievement.app.data.remote.dto.GameDto
import com.achievement.app.data.remote.dto.TrophyTitleDto
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.util.TimeUtils

object GameMapper {

    fun dtoToEntity(dto: GameDto): GameEntity {
        val playtimeMinutes = dto.playDuration?.let {
            TimeUtils.parseISO8601Duration(it)
        } ?: 0L

        return GameEntity(
            appId = dto.titleId,
            name = dto.displayName,
            platform = dto.platformString,
            playtimeMinutes = playtimeMinutes,
            headerImage = dto.imageUrl,
            lastPlayed = dto.lastPlayedDateTime
        )
    }

    fun entityToDomain(entity: GameEntity): Game {
        return Game(
            appId = entity.appId,
            name = entity.name,
            platform = entity.platform,
            playtimeMinutes = entity.playtimeMinutes,
            headerImage = entity.headerImage,
            lastPlayed = entity.lastPlayed,
            npCommunicationId = entity.npCommunicationId,
            npServiceName = entity.npServiceName
        )
    }

    fun enrichGameEntitiesWithTrophyData(
        gameEntities: List<GameEntity>,
        trophyTitles: List<TrophyTitleDto>
    ): List<GameEntity> {
        val trophyMap = trophyTitles.associateBy { it.npCommunicationId }
        return gameEntities.map { entity ->
            val matchedTrophy = trophyMap.values.find { title ->
                title.trophyTitleName == entity.name ||
                entity.npCommunicationId == title.npCommunicationId
            }
            if (matchedTrophy != null) {
                entity.copy(
                    npCommunicationId = matchedTrophy.npCommunicationId,
                    npServiceName = matchedTrophy.npServiceName
                )
            } else {
                entity
            }
        }
    }

    fun dtoListToEntityList(dtos: List<GameDto>): List<GameEntity> {
        return dtos.map { dtoToEntity(it) }
    }

    fun entityListToDomainList(entities: List<GameEntity>): List<Game> {
        return entities.map { entityToDomain(it) }
    }
}
