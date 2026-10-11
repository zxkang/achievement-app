package com.achievement.app.data.mapper

import com.achievement.app.data.local.entity.GameEntity
import com.achievement.app.data.remote.dto.GameDto
import com.achievement.app.data.remote.dto.TrophyTitleDto
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.util.PsnImageUrl
import com.achievement.app.domain.util.TimeUtils

object GameMapper {

    /**
     * 归一化游戏名用于跨接口匹配:去掉 ™®© 等商标符号、统一弯引号、折叠空白、
     * 去掉 Trophy 接口偶发附带的结尾 " Trophy/Trophies" 字样,并转小写。
     * 仅做精确匹配前的清洗,不做任何包含/部分匹配,避免误关联。
     */
    fun normalizeGameName(raw: String?): String {
        if (raw == null) return ""
        return raw
            .replace('’', '\'')   // ’
            .replace('‘', '\'')   // ‘
            .replace('“', '"')   // “
            .replace('”', '"')   // ”
            .replace("️", "")  // 变体选择符
            .replace(Regex("[™®©]"), "")
            .replace(Regex("[\\s　]+"), " ")
            .trim()
            .lowercase()
            .replace(Regex("\\s+(trophies|trophy)$"), "")
            .trim()
    }

    /**
     * 用英文 gamelist 与中文 gamelist 以 titleId 为键建立 英文名(归一化) -> 中文名 映射。
     */
    fun buildChineseNameMap(enTitles: List<GameDto>, zhTitles: List<GameDto>): Map<String, String> {
        val zhByTitleId = zhTitles.associate { it.titleId to (it.localizedName ?: it.name) }
        val map = HashMap<String, String>()
        enTitles.forEach { dto ->
            val en = dto.name ?: dto.localizedName ?: return@forEach
            val zh = zhByTitleId[dto.titleId] ?: dto.localizedName
            val key = normalizeGameName(en)
            if (key.isNotEmpty() && !zh.isNullOrBlank()) {
                map.putIfAbsent(key, zh)
            }
        }
        return map
    }

    /**
     * 依据英文 gamelist 构建游戏实体:展示名优先用中文名。奖杯关联(npCommunicationId)
     * 通过归一化名称精确匹配 Trophy 标题得到——Trophy 标题名可能是英文也可能是账号语言
     * 返回的中文,因此同时尝试英文名与中文展示名,任一命中即关联。
     */
    fun buildGameEntities(
        enTitles: List<GameDto>,
        chineseNameByNormalizedEnglish: Map<String, String>,
        trophyTitles: List<TrophyTitleDto>
    ): List<GameEntity> {
        // Trophy 标题名按归一化建索引(名称可能是任意语言)
        val trophyByName = trophyTitles.associateBy { normalizeGameName(it.trophyTitleName) }

        val built = enTitles.map { dto ->
            val enName = dto.name ?: dto.localizedName ?: dto.titleId
            val normalizedEn = normalizeGameName(enName)
            val displayName = chineseNameByNormalizedEnglish[normalizedEn] ?: dto.localizedName ?: enName
            val matchedTrophy = trophyByName[normalizedEn]
                ?: trophyByName[normalizeGameName(displayName)]
            val playtimeMinutes = dto.playDuration?.let { TimeUtils.parseISO8601Duration(it) } ?: 0L

            GameEntity(
                appId = dto.titleId,
                name = displayName,
                platform = dto.platformString,
                playtimeMinutes = playtimeMinutes,
                headerImage = PsnImageUrl.forceRaster(dto.imageUrl),
                lastPlayed = dto.lastPlayedDateTime,
                npCommunicationId = matchedTrophy?.npCommunicationId,
                npServiceName = matchedTrophy?.npServiceName
            )
        }

        return dedupeByDisplayName(built)
    }

    /**
     * 同一款游戏在 gamelist 里会以多个 titleId 出现(PS4/PS5、不同版本),
     * 按归一化展示名合并为一条:游玩时长取各变体最大值,最近游玩取最新,
     * 代表项优先保留有奖杯关联(npCommunicationId)、其次有缩略图的变体。
     */
    private fun dedupeByDisplayName(entities: List<GameEntity>): List<GameEntity> {
        val groups = LinkedHashMap<String, MutableList<GameEntity>>()
        for (entity in entities) {
            val key = normalizeGameName(entity.name).ifEmpty { entity.appId }
            groups.getOrPut(key) { mutableListOf() }.add(entity)
        }
        return groups.map { (_, variants) ->
            val representative = variants.maxWith(
                compareBy<GameEntity> { it.npCommunicationId != null }
                    .thenBy { !it.headerImage.isNullOrBlank() }
            )
            val maxPlaytime = variants.maxOf { it.playtimeMinutes }
            val latestPlayed = variants.mapNotNull { it.lastPlayed }.maxByOrNull { it }
            representative.copy(
                playtimeMinutes = maxPlaytime,
                lastPlayed = latestPlayed
            )
        }
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

    fun entityListToDomainList(entities: List<GameEntity>): List<Game> {
        return entities.map { entityToDomain(it) }
    }
}
