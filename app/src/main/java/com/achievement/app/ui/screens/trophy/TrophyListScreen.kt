package com.achievement.app.ui.screens.trophy

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.achievement.app.R
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.util.PsnImages
import com.achievement.app.domain.util.TimeUtils
import com.achievement.app.ui.theme.*
import com.achievement.app.ui.util.GameSort
import com.achievement.app.ui.util.GameSortSelector
import com.achievement.app.ui.util.applySort
import com.achievement.app.ui.util.rememberPsnImageModel
import com.achievement.app.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrophyListScreen(
    onGameClick: (appId: String, npCommunicationId: String?) -> Unit = { _, _ -> },
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var sort by remember { mutableStateOf(GameSort.Default) }

    val gamesWithAchievements = remember(uiState.games, sort) {
        uiState.games.filter { it.second != null }.applySort(sort)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "奖杯收藏",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PSNOnSurface
            )
        }

        uiState.player?.let { player ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PSNSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 20.dp)
                    ) {
                        TrophyStatItem("白金", player.platinumCount, Platinum, R.drawable.trophy_icon_platinum, Modifier.weight(1f))
                        TrophyStatItem("金", player.goldCount, Gold, R.drawable.trophy_icon_gold, Modifier.weight(1f))
                        TrophyStatItem("银", player.silverCount, Silver, R.drawable.trophy_icon_silver, Modifier.weight(1f))
                        TrophyStatItem("铜", player.bronzeCount, Bronze, R.drawable.trophy_icon_bronze, Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            GameSortSelector(
                selected = sort,
                onSelect = { sort = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(gamesWithAchievements, key = { it.first.appId }) { (game, achievement) ->
            if (achievement != null) {
                TrophyGameCard(
                    game = game,
                    achievement = achievement,
                    onClick = { onGameClick(game.appId, game.npCommunicationId) }
                )
            }
        }

        if (gamesWithAchievements.isEmpty() && !uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = PSNOnSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "暂无奖杯数据",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PSNOnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "到「我的」页面同步数据",
                            style = MaterialTheme.typography.bodySmall,
                            color = PSNOnSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrophyStatItem(
    label: String,
    count: Int,
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
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PSNOnSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrophyGameCard(
    game: Game,
    achievement: Achievement,
    onClick: () -> Unit
) {
    val lastPlayedText = game.lastPlayed?.let {
        TimeUtils.formatRelativeTime(TimeUtils.parseISO8601DateTime(it))
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = PSNSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = rememberPsnImageModel(game.headerImage, PsnImages.GAME_THUMB_PX),
                contentDescription = game.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PSNOnSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = buildString {
                        append("游玩 ${game.playtimeFormatted}")
                        if (lastPlayedText != null) append(" · 最近 $lastPlayedText")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = PSNOnSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniTrophy(achievement.platinumEarned, R.drawable.trophy_icon_platinum)
                    MiniTrophy(achievement.goldEarned, R.drawable.trophy_icon_gold)
                    MiniTrophy(achievement.silverEarned, R.drawable.trophy_icon_silver)
                    MiniTrophy(achievement.bronzeEarned, R.drawable.trophy_icon_bronze)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { achievement.progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = PSNBlue,
                    trackColor = PSNSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${achievement.progressPercent}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PSNBlueLight
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${achievement.achievementUnlocked}/${achievement.achievementTotal}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PSNOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MiniTrophy(count: Int, iconRes: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = PSNOnSurface
        )
    }
}
