package com.achievement.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.achievement.app.domain.util.TimeUtils
import com.achievement.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(
    game: Game,
    achievement: Achievement?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PSNSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            game.headerImage?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = game.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PSNOnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = game.platform,
                        style = MaterialTheme.typography.labelSmall,
                        color = PSNBlueLight
                    )
                    if (game.lastPlayed != null) {
                        val lastPlayedText = TimeUtils.formatRelativeTime(
                            TimeUtils.parseISO8601DateTime(game.lastPlayed)
                        )
                        Text(
                            text = "·",
                            color = PSNOnSurfaceVariant
                        )
                        Text(
                            text = "最近 $lastPlayedText",
                            style = MaterialTheme.typography.labelSmall,
                            color = PSNOnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "游玩时长",
                            style = MaterialTheme.typography.labelSmall,
                            color = PSNOnSurfaceVariant
                        )
                        Text(
                            text = game.playtimeFormatted,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = PSNOnSurface
                        )
                    }

                    achievement?.let {
                        TrophyProgress(achievement = it)
                    }
                }
            }
        }
    }
}

@Composable
fun TrophyProgress(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TrophyBadge(count = achievement.platinumEarned, color = Platinum, iconRes = R.drawable.trophy_icon_platinum)
            TrophyBadge(count = achievement.goldEarned, color = Gold, iconRes = R.drawable.trophy_icon_gold)
            TrophyBadge(count = achievement.silverEarned, color = Silver, iconRes = R.drawable.trophy_icon_silver)
            TrophyBadge(count = achievement.bronzeEarned, color = Bronze, iconRes = R.drawable.trophy_icon_bronze)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${achievement.progressPercent}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = PSNBlueLight
        )

        Spacer(modifier = Modifier.height(2.dp))

        LinearProgressIndicator(
            progress = { achievement.progressPercent / 100f },
            modifier = Modifier
                .width(100.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = PSNBlue,
            trackColor = PSNSurfaceVariant,
        )
    }
}

@Composable
fun TrophyBadge(
    count: Int,
    color: Color,
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
            modifier = Modifier.size(16.dp),
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
