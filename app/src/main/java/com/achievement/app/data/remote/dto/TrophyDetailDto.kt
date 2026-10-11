package com.achievement.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TitleTrophiesResponse(
    @SerialName("trophySetVersion") val trophySetVersion: String = "",
    @SerialName("hasTrophyGroups") val hasTrophyGroups: Boolean = false,
    @SerialName("trophies") val trophies: List<TrophyDetailDto> = emptyList(),
    @SerialName("totalItemCount") val totalItemCount: Int = 0,
    @SerialName("nextOffset") val nextOffset: Int? = null
)

@Serializable
data class TrophyDetailDto(
    @SerialName("trophyId") val trophyId: Int,
    @SerialName("trophyHidden") val trophyHidden: Boolean = false,
    @SerialName("trophyType") val trophyType: String,
    @SerialName("trophyName") val trophyName: String = "",
    @SerialName("trophyDetail") val trophyDetail: String = "",
    @SerialName("trophyIconUrl") val trophyIconUrl: String? = null,
    @SerialName("trophyEarnedRate") val trophyEarnedRate: String = "0",
    @SerialName("trophyGroupId") val trophyGroupId: String = "default"
)

@Serializable
data class UserEarnedTrophiesResponse(
    @SerialName("trophySetVersion") val trophySetVersion: String = "",
    @SerialName("hasTrophyGroups") val hasTrophyGroups: Boolean = false,
    @SerialName("trophies") val trophies: List<UserEarnedTrophyDto> = emptyList(),
    @SerialName("totalItemCount") val totalItemCount: Int = 0,
    @SerialName("nextOffset") val nextOffset: Int? = null
)

@Serializable
data class UserEarnedTrophyDto(
    @SerialName("trophyId") val trophyId: Int,
    @SerialName("trophyHidden") val trophyHidden: Boolean = false,
    @SerialName("earned") val earned: Boolean = false,
    @SerialName("earnedDateTime") val earnedDateTime: String? = null,
    @SerialName("trophyType") val trophyType: String = "",
    @SerialName("trophyRare") val trophyRare: Int = 0,
    @SerialName("trophyEarnedRate") val trophyEarnedRate: String = "0"
)
