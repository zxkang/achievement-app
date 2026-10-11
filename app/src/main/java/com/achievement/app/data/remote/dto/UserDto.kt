package com.achievement.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    @SerialName("onlineId") val onlineId: String,
    @SerialName("aboutMe") val aboutMe: String = "",
    @SerialName("avatars") val avatars: List<AvatarDto> = emptyList(),
    @SerialName("languages") val languages: List<String> = emptyList(),
    @SerialName("isPlus") val isPlus: Boolean = false,
    @SerialName("isOfficiallyVerified") val isOfficiallyVerified: Boolean = false,
    @SerialName("isMe") val isMe: Boolean = false
)

@Serializable
data class AvatarDto(
    @SerialName("size") val size: String,
    @SerialName("url") val url: String
) {
    val httpsUrl: String
        get() = url.replace("http://", "https://")
}

@Serializable
data class BasicPresenceResponse(
    @SerialName("availability") val availability: String = "unavailable",
    @SerialName("primaryPlatformInfo") val primaryPlatformInfo: PlatformInfoDto? = null,
    @SerialName("gameTitleInfoList") val gameTitleInfoList: List<GameTitleInfoDto> = emptyList()
)

@Serializable
data class PlatformInfoDto(
    @SerialName("onlineStatus") val onlineStatus: String = "offline",
    @SerialName("platform") val platform: String = "",
    @SerialName("lastOnlineDate") val lastOnlineDate: String? = null
)

@Serializable
data class GameTitleInfoDto(
    @SerialName("npTitleId") val npTitleId: String = "",
    @SerialName("titleName") val titleName: String = "",
    @SerialName("format") val format: String = "",
    @SerialName("launchPlatform") val launchPlatform: String? = null,
    @SerialName("iconUrl") val iconUrl: String? = null,
    @SerialName("smallIconUrl") val smallIconUrl: String? = null
)
