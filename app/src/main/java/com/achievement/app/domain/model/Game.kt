package com.achievement.app.domain.model

data class Game(
    val appId: String,
    val name: String,
    val platform: String,
    val playtimeMinutes: Long,
    val headerImage: String?,
    val lastPlayed: String?,
    val npCommunicationId: String? = null,
    val npServiceName: String? = null
) {
    val playtimeFormatted: String
        get() {
            val hours = playtimeMinutes / 60
            val minutes = playtimeMinutes % 60
            return when {
                hours > 0 && minutes > 0 -> "${hours}小时${minutes}分钟"
                hours > 0 -> "${hours}小时"
                else -> "${minutes}分钟"
            }
        }
}
