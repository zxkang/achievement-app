package com.achievement.app.data.mapper

import com.achievement.app.data.local.entity.AchievementEntity
import com.achievement.app.data.local.entity.PlatinumHallEntity
import com.achievement.app.data.remote.dto.TrophyTitleDto
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.PlatinumHall
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TrophyMapper {
    
    fun dtoToAchievementEntity(dto: TrophyTitleDto): AchievementEntity {
        val definedTotal = dto.definedTrophies.total
        val earnedTotal = dto.earnedTrophies.total
        
        return AchievementEntity(
            appId = dto.npCommunicationId,
            achievementTotal = definedTotal,
            achievementUnlocked = earnedTotal,
            platinumTotal = dto.definedTrophies.platinum,
            platinumEarned = dto.earnedTrophies.platinum,
            goldTotal = dto.definedTrophies.gold,
            goldEarned = dto.earnedTrophies.gold,
            silverTotal = dto.definedTrophies.silver,
            silverEarned = dto.earnedTrophies.silver,
            bronzeTotal = dto.definedTrophies.bronze,
            bronzeEarned = dto.earnedTrophies.bronze
        )
    }
    
    fun entityToDomain(entity: AchievementEntity): Achievement {
        return Achievement(
            appId = entity.appId,
            achievementTotal = entity.achievementTotal,
            achievementUnlocked = entity.achievementUnlocked,
            platinumTotal = entity.platinumTotal,
            platinumEarned = entity.platinumEarned,
            goldTotal = entity.goldTotal,
            goldEarned = entity.goldEarned,
            silverTotal = entity.silverTotal,
            silverEarned = entity.silverEarned,
            bronzeTotal = entity.bronzeTotal,
            bronzeEarned = entity.bronzeEarned
        )
    }
    
    fun dtoToPlatinumHallEntity(dto: TrophyTitleDto): PlatinumHallEntity? {
        if (dto.earnedTrophies.platinum == 0) return null
        
        val lastUpdated = dto.lastUpdatedDateTime?.let {
            try {
                java.time.Instant.parse(it)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            } catch (e: Exception) {
                LocalDateTime.now()
            }
        } ?: LocalDateTime.now()
        
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        
        return PlatinumHallEntity(
            appId = dto.npCommunicationId,
            gameName = dto.trophyTitleName,
            platinumEarnedDate = lastUpdated.format(formatter),
            year = lastUpdated.year
        )
    }
    
    fun entityToPlatinumHallDomain(entity: PlatinumHallEntity): PlatinumHall {
        return PlatinumHall(
            appId = entity.appId,
            gameName = entity.gameName,
            platinumEarnedDate = entity.platinumEarnedDate,
            year = entity.year
        )
    }
    
    fun dtoListToAchievementEntityList(dtos: List<TrophyTitleDto>): List<AchievementEntity> {
        return dtos.map { dtoToAchievementEntity(it) }
    }
    
    fun entityListToDomainList(entities: List<AchievementEntity>): List<Achievement> {
        return entities.map { entityToDomain(it) }
    }
    
    fun platinumEntityListToDomainList(entities: List<PlatinumHallEntity>): List<PlatinumHall> {
        return entities.map { entityToPlatinumHallDomain(it) }
    }
}
