package com.achievement.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameListResponse(
    @SerialName("titles") val titles: List<GameDto> = emptyList(),
    @SerialName("size") val size: Int = 0,
    @SerialName("nextOffset") val nextOffset: Int? = null
)

@Serializable
data class GameDto(
    @SerialName("titleId") val titleId: String,
    @SerialName("localizedName") val localizedName: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("playDuration") val playDuration: String? = null,
    @SerialName("lastPlayedDateTime") val lastPlayedDateTime: String? = null,
    @SerialName("platform") val platform: List<String> = emptyList()
) {
    val displayName: String
        get() = localizedName ?: name ?: titleId
    
    val platformString: String
        get() = platform.joinToString(", ")
}
