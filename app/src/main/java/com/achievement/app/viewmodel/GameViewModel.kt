package com.achievement.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achievement.app.data.repository.GameRepository
import com.achievement.app.data.repository.PlayerRepository
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.model.PlatinumHall
import com.achievement.app.domain.model.Player
import com.achievement.app.domain.util.SyncThrottle
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val playerRepository: PlayerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
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
            }
        }
    }

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
            
            val result = gameRepository.syncGamesFromApi()
            // 同步账号级奖杯汇总,保证顶部总数与官方一致
            playerRepository.syncPlayerFromApi()
            
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
