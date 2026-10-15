package com.achievement.app.ui.screens.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.achievement.app.ui.components.GameCard
import com.achievement.app.ui.theme.*
import com.achievement.app.ui.util.CollapsibleHeader
import com.achievement.app.ui.util.GameSort
import com.achievement.app.ui.util.GameSortDropdown
import com.achievement.app.ui.util.applySort
import com.achievement.app.ui.util.expand
import com.achievement.app.ui.util.rememberCollapsibleHeaderState
import com.achievement.app.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onGameClick: (appId: String, npCommunicationId: String?) -> Unit = { _, _ -> },
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableStateOf(GameSort.Default) }
    val header = rememberCollapsibleHeaderState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 首次组合(含从详情页返回重建)时不回顶,交给 rememberLazyListState 恢复滚动位置;
    // 之后用户主动改搜索/排序才回到顶部并展开头部
    var filtersInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(searchQuery, sort) {
        if (!filtersInitialized) {
            filtersInitialized = true
            return@LaunchedEffect
        }
        listState.scrollToItem(0)
        header.expand()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val filteredGames = remember(uiState.games, searchQuery, sort) {
        val base = if (searchQuery.isBlank()) {
            uiState.games
        } else {
            uiState.games.filter { (game, _) ->
                game.name.contains(searchQuery, ignoreCase = true)
            }
        }
        base.applySort(sort)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = contentPadding.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(header.connection)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "游戏",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PSNOnSurface
                    )
                    if (uiState.games.isNotEmpty()) {
                        Text(
                            text = "共 ${uiState.games.size} 款",
                            style = MaterialTheme.typography.labelMedium,
                            color = PSNOnSurfaceVariant,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                CollapsibleHeader(header) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = PSNOnSurface
                            ),
                            cursorBrush = SolidColor(PSNBlueLight),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(PSNSurfaceVariant.copy(alpha = 0.35f)),
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = PSNOnSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 10.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                "搜索游戏",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = PSNOnSurfaceVariant
                                            )
                                        }
                                        innerTextField()
                                    }
                                    if (searchQuery.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .clickable { searchQuery = "" },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "清除搜索",
                                                tint = PSNOnSurfaceVariant,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        )

                        GameSortDropdown(
                            selected = sort,
                            onSelect = { sort = it }
                        )
                    }
                }

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredGames, key = { it.first.appId }) { (game, achievement) ->
                        GameCard(
                            game = game,
                            achievement = achievement,
                            onClick = { onGameClick(game.appId, game.npCommunicationId) }
                        )
                    }

                    if (filteredGames.isEmpty() && !uiState.isLoading) {
                        item {
                            EmptyGamesState(searching = searchQuery.isNotBlank())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyGamesState(searching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.SentimentDissatisfied,
            contentDescription = null,
            tint = PSNOnSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (searching) "未找到匹配的游戏" else "暂无游戏数据",
            style = MaterialTheme.typography.bodyLarge,
            color = PSNOnSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (searching) "换个关键词试试" else "到「我的」页面同步数据",
            style = MaterialTheme.typography.bodySmall,
            color = PSNOnSurfaceVariant
        )
    }
}
