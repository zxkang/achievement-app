package com.achievement.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achievement.app.data.repository.GameRepository
import com.achievement.app.data.repository.PlayerRepository
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.model.PlatinumHall
import com.achievement.app.domain.model.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val games: List<Pair<Game, Achievement?>> = emptyList(),
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
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
