package com.achievement.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.achievement.app.R
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.util.PsnImages
import com.achievement.app.domain.util.TimeUtils
import com.achievement.app.ui.theme.*
import com.achievement.app.ui.util.rememberPsnImageModel

/**
 * 游戏库列表行:左侧横幅封面,右侧信息区(名称/元信息/奖杯与进度)。
 * 一行一卡,单屏可见 5~6 项,信息密度对齐 PS App 的库列表。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(
    game: Game,
    achievement: Achievement?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = PSNSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PSNSurfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 116.dp, height = 88.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PSNSurfaceVariant)
            ) {
                // 占位图标垫底,加载失败时依然可见(图片成功会盖住它)
                Icon(
                    imageVector = Icons.Outlined.VideogameAsset,
                    contentDescription = null,
                    tint = PSNOnSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.align(Alignment.Center).size(26.dp)
                )
                AsyncImage(
                    model = rememberPsnImageModel(game.headerImage, PsnImages.GAME_THUMB_PX),
                    contentDescription = game.name,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PSNOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (game.platform.isNotBlank()) {
                        Text(
                            text = game.platform,
                            style = MaterialTheme.typography.labelSmall,
                            color = PSNBlueLight
                        )
                        Text(
                            text = " · ",
                            style = MaterialTheme.typography.labelSmall,
                            color = PSNOnSurfaceVariant
                        )
                    }
                    Text(
                        text = game.playtimeFormatted,
                        style = MaterialTheme.typography.labelSmall,
                        color = PSNOnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    game.lastPlayed?.let { iso ->
                        Text(
                            text = " · 最近 ${TimeUtils.formatRelativeTime(TimeUtils.parseISO8601DateTime(iso))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PSNOnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                achievement?.let {
                    TrophySummary(achievement = it, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

/** 第一行奖杯计数,第二行通栏进度条 + 百分比,避免窄列下互相挤压。 */
@Composable
fun TrophySummary(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    val complete = achievement.progressPercent >= 100
    val barColor = if (complete) Success else PSNBlue
    val textColor = if (complete) Success else PSNBlueLight
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TrophyBadge(count = achievement.platinumEarned, iconRes = R.drawable.trophy_icon_platinum)
            TrophyBadge(count = achievement.goldEarned, iconRes = R.drawable.trophy_icon_gold)
            TrophyBadge(count = achievement.silverEarned, iconRes = R.drawable.trophy_icon_silver)
            TrophyBadge(count = achievement.bronzeEarned, iconRes = R.drawable.trophy_icon_bronze)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LinearProgressIndicator(
                progress = { achievement.progressPercent / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = barColor,
                trackColor = PSNSurfaceVariant,
            )
            Text(
                text = "${achievement.progressPercent}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false,
                color = textColor
            )
        }
    }
}

@Composable
fun TrophyBadge(
    count: Int,
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = PSNOnSurface
        )
    }
}
