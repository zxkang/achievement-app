package com.achievement.app.ui.screens.gamedetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.achievement.app.R
import com.achievement.app.ui.components.TrophyItemCard
import com.achievement.app.ui.theme.*
import com.achievement.app.viewmodel.GameDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.game?.npCommunicationId) {
        val npId = uiState.game?.npCommunicationId
        if (npId != null && uiState.trophies.isEmpty()) {
            viewModel.syncTrophies()
        }
    }

    Scaffold(
        containerColor = PSNBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.game?.name ?: "游戏详情",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = PSNOnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = PSNOnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PSNBackground
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoadingTrophies,
            onRefresh = { viewModel.syncTrophies() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.game?.let { game ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PSNSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column {
                                if (game.headerImage != null) {
                                    AsyncImage(
                                        model = game.headerImage,
                                        contentDescription = game.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = game.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = PSNOnSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = game.platform,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PSNBlueLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "游玩时长: ${game.playtimeFormatted}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PSNOnSurfaceVariant
                                    )

                                    uiState.achievement?.let { achievement ->
                                        Spacer(modifier = Modifier.height(12.dp))

                                        LinearProgressIndicator(
                                            progress = { achievement.progressPercent / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = PSNBlue,
                                            trackColor = PSNSurfaceVariant,
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${achievement.progressPercent}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = PSNBlueLight
                                            )
                                            Text(
                                                text = "${achievement.achievementUnlocked}/${achievement.achievementTotal}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PSNOnSurfaceVariant
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            DetailTrophyStat("白金", achievement.platinumEarned, achievement.platinumTotal, Platinum, R.drawable.trophy_icon_platinum)
                                            DetailTrophyStat("金", achievement.goldEarned, achievement.goldTotal, Gold, R.drawable.trophy_icon_gold)
                                            DetailTrophyStat("银", achievement.silverEarned, achievement.silverTotal, Silver, R.drawable.trophy_icon_silver)
                                            DetailTrophyStat("铜", achievement.bronzeEarned, achievement.bronzeTotal, Bronze, R.drawable.trophy_icon_bronze)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (uiState.trophies.isNotEmpty()) {
                    item {
                        Text(
                            text = "奖杯列表 (${uiState.trophies.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PSNOnSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(uiState.trophies) { trophy ->
                        TrophyItemCard(trophy = trophy)
                    }
                }

                if (uiState.trophies.isEmpty() && !uiState.isLoadingTrophies) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (uiState.game?.npCommunicationId == null) {
                                    Text(
                                        text = "该游戏暂无奖杯数据",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = PSNOnSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        text = "下拉刷新加载奖杯详情",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = PSNOnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                uiState.error?.let { error ->
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            )
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailTrophyStat(
    label: String,
    earned: Int,
    total: Int,
    color: Color,
    iconRes: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "$earned/$total",
            style = MaterialTheme.typography.bodyMedium,
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
