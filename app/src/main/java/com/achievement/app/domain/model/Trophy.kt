package com.achievement.app.domain.model

data class Trophy(
    val trophyId: Int,
    val appId: String,
    val name: String,
    val detail: String,
    val type: String,
    val iconUrl: String?,
    val isHidden: Boolean,
    val earnedRate: Double,
    val groupId: String,
    val isEarned: Boolean,
    val earnedDateTime: String?,
    val rarity: String
) {
    val typeColor: String
        get() = when (type) {
            "platinum" -> "platinum"
            "gold" -> "gold"
            "silver" -> "silver"
            "bronze" -> "bronze"
            else -> "bronze"
        }

    val displayName: String
        get() = if (isHidden && !isEarned) "???" else name

    val displayDetail: String
        get() = if (isHidden && !isEarned) "隐藏奖杯" else detail

    val rarityLabel: String
        get() = when (rarity) {
            "ultraRare" -> "极稀有"
            "veryRare" -> "非常稀有"
            "rare" -> "稀有"
            "common" -> "普通"
            else -> ""
        }
}
