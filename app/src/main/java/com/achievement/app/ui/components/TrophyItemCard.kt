package com.achievement.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.achievement.app.R
import com.achievement.app.domain.model.Trophy
import com.achievement.app.domain.util.PsnImages
import com.achievement.app.ui.util.rememberPsnImageModel
import com.achievement.app.ui.theme.*

@Composable
fun TrophyItemCard(
    trophy: Trophy,
    modifier: Modifier = Modifier
) {
    val typeColor = when (trophy.type) {
        "platinum" -> Platinum
        "gold" -> Gold
        "silver" -> Silver
        "bronze" -> Bronze
        else -> Bronze
    }

    val rarityColor = when (trophy.rarity) {
        "ultraRare" -> Color(0xFFE040FB)
        "veryRare" -> Color(0xFFFF6D00)
        "rare" -> Color(0xFF42A5F5)
        "common" -> PSNOnSurfaceVariant
        else -> PSNOnSurfaceVariant
    }

    val isPlatinum = trophy.type == "platinum"
    val iconModel = rememberPsnImageModel(trophy.iconUrl, PsnImages.TROPHY_ICON_PX)
    // PSN 图标服务只有 HTTP/1.1 且首字节抖动大(1~7s),整屏图标不可能瞬间到位。
    // 先用本地对应类型的奖杯轮廓占位,避免下载期间只剩空圈。
    val fallbackIcon = painterResource(
        when (trophy.type) {
            "platinum" -> R.drawable.trophy_icon_platinum
            "gold" -> R.drawable.trophy_icon_gold
            "silver" -> R.drawable.trophy_icon_silver
            else -> R.drawable.trophy_icon_bronze
        }
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trophy.isEarned) PSNSurface else PSNSurface.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlatinum && trophy.isEarned) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (isPlatinum) 56.dp else 48.dp)
                    .then(
                        if (isPlatinum && trophy.isEarned) {
                            Modifier
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            typeColor.copy(alpha = 0.3f),
                                            typeColor.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .border(2.dp, typeColor, CircleShape)
                        } else {
                            Modifier
                                .background(typeColor.copy(alpha = 0.15f), CircleShape)
                        }
                    )
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = fallbackIcon,
                    contentDescription = null,
                    modifier = Modifier.size(if (isPlatinum) 32.dp else 26.dp),
                    contentScale = ContentScale.Fit,
                    alpha = if (trophy.isEarned) 0.85f else 0.35f
                )
                if (iconModel != null) {
                    AsyncImage(
                        model = iconModel,
                        contentDescription = null,
                        modifier = Modifier
                            .size(if (isPlatinum) 48.dp else 40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alpha = if (trophy.isEarned) 1f else 0.4f
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = trophy.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isPlatinum) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (trophy.isEarned) PSNOnSurface else PSNOnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (trophy.isEarned && trophy.rarityLabel.isNotEmpty()) {
                        Text(
                            text = trophy.rarityLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = rarityColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (trophy.isEarned || !trophy.isHidden) {
                    Text(
                        text = trophy.displayDetail,
                        style = MaterialTheme.typography.bodySmall,
                        color = PSNOnSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (trophy.isEarned && trophy.earnedDateTime != null) {
                    Text(
                        text = formatEarnedDate(trophy.earnedDateTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = PSNOnSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (trophy.earnedRate > 0) {
                    Text(
                        text = "${trophy.earnedRate.toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = PSNOnSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatEarnedDate(dateStr: String): String {
    return try {
        val instant = java.time.Instant.parse(dateStr)
        val ldt = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
        ldt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    } catch (e: Exception) {
        dateStr
    }
}
