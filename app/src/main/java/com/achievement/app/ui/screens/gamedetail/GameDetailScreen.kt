package com.achievement.app.ui.screens.gamedetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.achievement.app.R
import com.achievement.app.domain.util.PsnImages
import com.achievement.app.ui.components.TrophyItemCard
import com.achievement.app.ui.util.rememberPsnImageModel
import com.achievement.app.ui.theme.*
import com.achievement.app.viewmodel.GameDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    onBack: () -> Unit,
    viewModel: GameDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedFilter by remember { mutableIntStateOf(0) }
    val allTrophies = uiState.trophies
    val earnedCount = allTrophies.count { it.isEarned }
    val filteredTrophies = remember(allTrophies, selectedFilter) {
        when (selectedFilter) {
            1 -> allTrophies.filter { it.isEarned }
            2 -> allTrophies.filter { !it.isEarned }
            else -> allTrophies
        }
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                                        model = rememberPsnImageModel(game.headerImage, PsnImages.GAME_HEADER_PX),
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

                if (uiState.isLoadingTrophies && uiState.trophies.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = PSNBlue,
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "正在加载奖杯…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PSNOnSurfaceVariant
                                )
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

                    item {
                        val options = listOf(
                            "全部 ${uiState.trophies.size}",
                            "已获得 $earnedCount",
                            "未获得 ${uiState.trophies.size - earnedCount}"
                        )
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            options.forEachIndexed { index, label ->
                                SegmentedButton(
                                    selected = selectedFilter == index,
                                    onClick = { selectedFilter = index },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = options.size
                                    ),
                                    colors = SegmentedButtonDefaults.colors(
                                        activeContainerColor = PSNBlue.copy(alpha = 0.25f),
                                        activeContentColor = PSNBlueLight,
                                        inactiveContainerColor = PSNSurface,
                                        inactiveContentColor = PSNOnSurfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    items(filteredTrophies, key = { "${it.appId}:${it.groupId}:${it.trophyId}" }) { trophy ->
                        TrophyItemCard(trophy = trophy)
                    }

                    if (filteredTrophies.isEmpty()) {
                        item {
                            Text(
                                text = if (selectedFilter == 1) "暂无已获得的奖杯" else "暂无未获得的奖杯",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PSNOnSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                if (uiState.trophies.isEmpty() && !uiState.isLoadingTrophies) {
                    val gameName = uiState.game?.name.orEmpty()
                    val hasNp = uiState.game?.npCommunicationId != null
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = PSNOnSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = when {
                                    hasNp -> "还没有加载奖杯详情"
                                    isCollectionName(gameName) -> "这是一个游戏合集"
                                    else -> "该游戏暂无奖杯记录"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PSNOnSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when {
                                    hasNp -> "点击下方按钮同步该游戏的奖杯列表"
                                    isCollectionName(gameName) -> "合集内的奖杯按子游戏分别记录,请从对应的子游戏查看"
                                    else -> "该游戏可能没有奖杯,或尚未在你的账号中生成奖杯记录"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = PSNOnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            if (hasNp) {
                                Spacer(modifier = Modifier.height(20.dp))
                                FilledTonalButton(onClick = { viewModel.syncTrophies() }) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("加载奖杯")
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

/** 判断是否为合集/捆绑包类标题(其奖杯通常按子游戏分别记录)。 */
private fun isCollectionName(name: String): Boolean {
    val n = name.lowercase()
    return listOf("trilogy", "collection", "bundle", "合辑", "合集", "三部曲").any { n.contains(it) }
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
