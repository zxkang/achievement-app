package com.achievement.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achievement.app.data.repository.GameRepository
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.model.Trophy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameDetailUiState(
    val game: Game? = null,
    val achievement: Achievement? = null,
    val trophies: List<Trophy> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingTrophies: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val appId: String = java.net.URLDecoder.decode(savedStateHandle.get<String>("appId") ?: "", "UTF-8")
    private val rawNpId: String = savedStateHandle.get<String>("npCommunicationId") ?: "none"
    private val npCommunicationId: String? = if (rawNpId == "none") null else java.net.URLDecoder.decode(rawNpId, "UTF-8")

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    init {
        loadGameData()
        loadTrophies()
    }

    private fun loadGameData() {
        viewModelScope.launch {
            val game = gameRepository.getGameById(appId)
            val achievementKey = game?.npCommunicationId ?: appId
            val achievement = gameRepository.getAchievementById(achievementKey)
            _uiState.value = _uiState.value.copy(game = game, achievement = achievement)
        }
    }

    private fun loadTrophies() {
        viewModelScope.launch {
            gameRepository.getTrophiesForGame(appId).collect { trophies ->
                _uiState.value = _uiState.value.copy(trophies = trophies)
            }
        }
    }

    fun syncTrophies() {
        val npId = npCommunicationId ?: _uiState.value.game?.npCommunicationId
        if (npId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(error = "该游戏暂无奖杯数据")
            return
        }

        val npServiceName = _uiState.value.game?.npServiceName ?: "trophy"

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingTrophies = true, error = null)

            val result = gameRepository.syncTrophiesForGame(npId, appId, npServiceName)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoadingTrophies = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoadingTrophies = false,
                    error = e.message ?: "加载奖杯失败"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
