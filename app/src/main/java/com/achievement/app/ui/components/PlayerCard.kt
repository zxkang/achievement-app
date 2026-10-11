package com.achievement.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.achievement.app.R
import com.achievement.app.domain.model.Player
import com.achievement.app.ui.theme.*

@Composable
fun PlayerCard(
    player: Player,
    modifier: Modifier = Modifier,
    showTrophySummary: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PSNSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            player.avatarUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "头像",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = player.onlineId,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PSNOnSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (player.isOnline) Color(0xFF4CAF50) else PSNOnSurfaceVariant.copy(alpha = 0.4f))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (player.isOnline) "在线" else "离线",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (player.isOnline) Color(0xFF4CAF50) else PSNOnSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = PSNBlueLight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Lv.${player.trophyLevel} · ${player.tierName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PSNOnSurfaceVariant
                )
            }

            if (player.isPlus) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PSNBlue.copy(alpha = 0.15f)
                    )
                ) {
                    Text(
                        text = "PS Plus 会员",
                        style = MaterialTheme.typography.labelSmall,
                        color = PSNBlueLight,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (player.aboutMe.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = player.aboutMe,
                    style = MaterialTheme.typography.bodySmall,
                    color = PSNOnSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (player.isOnline && player.currentGame != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PSNSurfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "正在玩",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = player.currentGame ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = PSNOnSurface
                        )
                    }
                }
            }

            if (showTrophySummary) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TrophyStatCircle(count = player.platinumCount, label = "白金", color = Platinum, iconRes = R.drawable.trophy_icon_platinum)
                    TrophyStatCircle(count = player.goldCount, label = "金", color = Gold, iconRes = R.drawable.trophy_icon_gold)
                    TrophyStatCircle(count = player.silverCount, label = "银", color = Silver, iconRes = R.drawable.trophy_icon_silver)
                    TrophyStatCircle(count = player.bronzeCount, label = "铜", color = Bronze, iconRes = R.drawable.trophy_icon_bronze)
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { player.trophyProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PSNBlue,
                    trackColor = PSNSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "下一级进度 ${player.trophyProgress}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = PSNOnSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun TrophyStatCircle(
    count: Int,
    label: String,
    color: Color,
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PSNOnSurfaceVariant
        )
    }
}
