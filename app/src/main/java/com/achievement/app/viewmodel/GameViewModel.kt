package com.achievement.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achievement.app.data.repository.GameRepository
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.model.PlatinumHall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val games: List<Pair<Game, Achievement?>> = emptyList(),
    val platinumHall: List<PlatinumHall> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    init {
        loadGames()
        loadPlatinumHall()
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
    
    fun syncFromApi() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = gameRepository.syncGamesFromApi()
            
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
