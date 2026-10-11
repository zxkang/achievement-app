package com.achievement.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achievement.app.data.repository.PlayerRepository
import com.achievement.app.domain.model.Player
import com.achievement.app.domain.util.SyncThrottle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val player: Player? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadPlayer()
    }
    
    private fun loadPlayer() {
        viewModelScope.launch {
            playerRepository.getPlayer().collect { player ->
                _uiState.value = _uiState.value.copy(player = player)
            }
        }
    }
    
    fun syncFromApi() {
        SyncThrottle.mark(SyncThrottle.KEY_PLAYER)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = playerRepository.syncPlayerFromApi()

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
        if (SyncThrottle.isStale(SyncThrottle.KEY_PLAYER, SyncThrottle.PLAYER_INTERVAL_MS)) {
            syncFromApi()
        }
    }

    fun syncPresence() {
        SyncThrottle.mark(SyncThrottle.KEY_PRESENCE)
        viewModelScope.launch {
            playerRepository.syncPresence()
        }
    }

    fun autoSyncPresenceIfStale() {
        if (SyncThrottle.isStale(SyncThrottle.KEY_PRESENCE, SyncThrottle.PRESENCE_INTERVAL_MS)) {
            syncPresence()
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
