package com.achievement.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.achievement.app.ui.components.GameCard
import com.achievement.app.ui.components.PlayerCard
import com.achievement.app.ui.theme.*
import com.achievement.app.viewmodel.GameViewModel
import com.achievement.app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGameClick: (appId: String, npCommunicationId: String?) -> Unit = { _, _ -> },
    gameViewModel: GameViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val profileUiState by profileViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        gameViewModel.autoSyncIfStale()
        profileViewModel.autoSyncPresenceIfStale()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "PSN 成就管理",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PSNOnSurface
            )
        }

        profileUiState.player?.let { player ->
            item {
                PlayerCard(player = player)
            }
        }

        item {
            Text(
                text = "最近游玩",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PSNOnSurface
            )
        }

        items(gameUiState.games.take(5)) { (game, achievement) ->
            GameCard(
                game = game,
                achievement = achievement,
                onClick = { onGameClick(game.appId, game.npCommunicationId) }
            )
        }

        if (gameUiState.games.isEmpty() && !gameUiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "暂无游戏数据",
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
