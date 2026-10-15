package com.achievement.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achievement.app.data.repository.GameRepository
import com.achievement.app.data.repository.PlayerRepository
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.model.PlatinumHall
import com.achievement.app.domain.model.Player
import com.achievement.app.domain.util.PsnImageUrl
import com.achievement.app.domain.util.PsnImages
import com.achievement.app.domain.util.SyncThrottle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val games: List<Pair<Game, Achievement?>> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val platinumHall: List<PlatinumHall> = emptyList(),
    val player: Player? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val playerRepository: PlayerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val prefetchedImages = mutableSetOf<String>()
    
    init {
        loadGames()
        loadAchievements()
        loadPlatinumHall()
        loadPlayer()
    }
    
    private fun loadGames() {
        viewModelScope.launch {
            gameRepository.getGamesWithAchievements().collect { games ->
                _uiState.value = _uiState.value.copy(games = games)
                prefetchHeaders(games)
            }
        }
    }

    /** PSN CDN 首字节延迟高,提前把列表将展示的图片限流拉进缓存,滚动时直接命中。 */
    private fun prefetchHeaders(games: List<Pair<Game, Achievement?>>) {
        // 列表行封面按 220px 小图展示,只预取小图;800px 原图仅详情页使用。
        val thumbs = games.take(40).mapNotNull {
            PsnImageUrl.sized(it.first.headerImage, PsnImages.GAME_THUMB_PX)
        }
        PsnImages.prefetch(viewModelScope, context, unseen(thumbs), PsnImages.GAME_THUMB_PX, PsnImages.COVER_PARALLELISM)
    }

    /** Room 的游戏流会反复发射,过滤掉已预取的地址避免重复排队。 */
    private fun unseen(urls: List<String>): List<String> = urls.filter { prefetchedImages.add(it) }

    private fun loadAchievements() {
        viewModelScope.launch {
            gameRepository.getAllAchievements().collect { achievements ->
                _uiState.value = _uiState.value.copy(achievements = achievements)
            }
        }
    }
    
    private fun loadPlatinumHall() {
        viewModelScope.launch {
            gameRepository.getPlatinumHall().collect { platinumHalls ->
                _uiState.value = _uiState.value.copy(platinumHall = platinumHalls)
            }
        }
    }

    private fun loadPlayer() {
        viewModelScope.launch {
            playerRepository.getPlayer().collect { player ->
                _uiState.value = _uiState.value.copy(player = player)
            }
        }
    }
    
    fun syncFromApi() {
        SyncThrottle.mark(SyncThrottle.KEY_GAMES)
        SyncThrottle.mark(SyncThrottle.KEY_PLAYER)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // 游戏列表与账号奖杯汇总并发同步,减少一次下拉刷新的总耗时
            val gamesTask = async { gameRepository.syncGamesFromApi() }
            // 同步账号级奖杯汇总,保证顶部总数与官方一致
            val playerTask = async { playerRepository.syncPlayerFromApi() }
            val result = gamesTask.await()
            playerTask.await()
            
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "同步失败"
                )
            }
        }
    }
    
    fun autoSyncIfStale() {
        if (SyncThrottle.isStale(SyncThrottle.KEY_GAMES, SyncThrottle.GAMES_INTERVAL_MS)) {
            syncFromApi()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
