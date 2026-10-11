package com.achievement.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrophyTitlesResponse(
    @SerialName("trophyTitles") val trophyTitles: List<TrophyTitleDto> = emptyList(),
    @SerialName("totalSize") val totalSize: Int = 0,
    @SerialName("nextOffset") val nextOffset: Int? = null
)

@Serializable
data class TrophyTitleDto(
    @SerialName("npServiceName") val npServiceName: String,
    @SerialName("npCommunicationId") val npCommunicationId: String,
    @SerialName("trophyTitleName") val trophyTitleName: String,
    @SerialName("trophyTitleIconUrl") val trophyTitleIconUrl: String? = null,
    @SerialName("trophyTitlePlatform") val trophyTitlePlatform: String,
    @SerialName("definedTrophies") val definedTrophies: TrophyCountsDto,
    @SerialName("earnedTrophies") val earnedTrophies: TrophyCountsDto,
    @SerialName("progress") val progress: Int = 0,
    @SerialName("lastUpdatedDateTime") val lastUpdatedDateTime: String? = null
)

@Serializable
data class TrophyCountsDto(
    @SerialName("platinum") val platinum: Int = 0,
    @SerialName("gold") val gold: Int = 0,
    @SerialName("silver") val silver: Int = 0,
    @SerialName("bronze") val bronze: Int = 0
) {
    val total: Int
        get() = platinum + gold + silver + bronze
}

@Serializable
data class TrophySummaryResponse(
    @SerialName("accountId") val accountId: String,
    @SerialName("trophyLevel") val trophyLevel: Int,
    @SerialName("progress") val progress: Int,
    @SerialName("tier") val tier: Int,
    @SerialName("earnedTrophies") val earnedTrophies: TrophyCountsDto
)
