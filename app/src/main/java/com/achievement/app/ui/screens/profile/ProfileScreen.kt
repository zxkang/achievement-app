package com.achievement.app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.achievement.app.R
import com.achievement.app.domain.util.TimeUtils
import com.achievement.app.ui.components.PlayerCard
import com.achievement.app.ui.theme.*
import com.achievement.app.viewmodel.GameViewModel
import com.achievement.app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    gameViewModel: GameViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val gameUiState by gameViewModel.uiState.collectAsState()

    val games = gameUiState.games
    val totalPlaytimeMinutes = games.sumOf { it.first.playtimeMinutes }
    val totalGames = games.size
    // 完美数以奖杯标题数据为准:gamelist 可能缺少个别已 100% 的标题,故不依赖 games 配对
    val perfectCount = gameUiState.achievements.count {
        it.achievementTotal > 0 && it.achievementUnlocked == it.achievementTotal
    }
    val completionRate = if (totalGames > 0) perfectCount * 100 / totalGames else 0
    val lastSyncText = uiState.player?.lastSyncAt?.takeIf { it > 0 }?.let {
        java.time.Instant.ofEpochMilli(it)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()
            .let(TimeUtils::formatDateTime)
    }

    LaunchedEffect(Unit) {
        gameViewModel.autoSyncIfStale()
        viewModel.autoSyncIfStale()
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading || gameUiState.isLoading,
        onRefresh = {
            viewModel.syncFromApi()
            gameViewModel.syncFromApi()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.player?.let { player ->
                item {
                    PlayerCard(
                        player = player,
                        showTrophySummary = false
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PSNSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "奖杯统计",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PSNOnSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ProfileTrophyStat("白金", player.platinumCount, Platinum, R.drawable.trophy_icon_platinum)
                                ProfileTrophyStat("金", player.goldCount, Gold, R.drawable.trophy_icon_gold)
                                ProfileTrophyStat("银", player.silverCount, Silver, R.drawable.trophy_icon_silver)
                                ProfileTrophyStat("铜", player.bronzeCount, Bronze, R.drawable.trophy_icon_bronze)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(color = PSNSurfaceVariant)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ProfileValueStat("总奖杯", player.totalTrophies.toString(), PSNOnSurface)
                                ProfileValueStat("等级", "Lv.${player.trophyLevel}", PSNBlueLight)
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

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "下一级进度",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PSNOnSurfaceVariant
                                )
                                Text(
                                    text = "${player.trophyProgress}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PSNBlueLight
                                )
                            }

                            if (games.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(20.dp))

                                HorizontalDivider(color = PSNSurfaceVariant)

                                Spacer(modifier = Modifier.height(18.dp))

                                Text(
                                    text = "游戏统计",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PSNOnSurface
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = TimeUtils.formatPlayTime(totalPlaytimeMinutes),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PSNBlueLight
                                    )
                                    Text(
                                        text = "总游玩时长",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PSNOnSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    ProfileValueStat("总游戏数", totalGames.toString(), PSNOnSurface)
                                    ProfileValueStat("完美数", perfectCount.toString(), PSNOnSurface)
                                    ProfileValueStat("完成率", "$completionRate%", PSNBlueLight)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = PSNOnSurfaceVariant
                                        )
                                        Text(
                                            text = "最近更新",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PSNOnSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = lastSyncText ?: "未同步",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = PSNOnSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.player == null && !uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无玩家数据",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PSNOnSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.error
                            )
                        )
                    )
                ) {
                    Text("退出登录", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProfileTrophyStat(label: String, count: Int, color: Color, iconRes: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(36.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
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

@Composable
private fun ProfileValueStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
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
