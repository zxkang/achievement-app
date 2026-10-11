package com.achievement.app.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.achievement.app.ui.components.GameCard
import com.achievement.app.ui.theme.*
import com.achievement.app.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onGameClick: (appId: String, npCommunicationId: String?) -> Unit = { _, _ -> },
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredGames = remember(uiState.games, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.games
        } else {
            uiState.games.filter { (game, _) ->
                game.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.syncFromApi() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("搜索游戏...", color = PSNOnSurfaceVariant) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = PSNOnSurfaceVariant)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PSNBlue,
                    unfocusedBorderColor = PSNSurfaceVariant,
                    cursorColor = PSNBlue,
                    focusedTextColor = PSNOnSurface,
                    unfocusedTextColor = PSNOnSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGames) { (game, achievement) ->
                    GameCard(
                        game = game,
                        achievement = achievement,
                        onClick = { onGameClick(game.appId, game.npCommunicationId) }
                    )
                }

                if (filteredGames.isEmpty() && !uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "暂无游戏数据" else "未找到匹配的游戏",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PSNOnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
