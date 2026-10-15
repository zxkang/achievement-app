package com.achievement.app.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achievement.app.data.repository.GameRepository
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.domain.model.Trophy
import com.achievement.app.domain.util.PsnImageUrl
import com.achievement.app.domain.util.PsnImages
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val appId: String = java.net.URLDecoder.decode(savedStateHandle.get<String>("appId") ?: "", "UTF-8")
    private val rawNpId: String = savedStateHandle.get<String>("npCommunicationId") ?: "none"
    private val npCommunicationId: String? = if (rawNpId == "none") null else java.net.URLDecoder.decode(rawNpId, "UTF-8")

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    /** Room 的奖杯流会因同步反复发射,记录已预取的地址避免重复排队。 */
    private val prefetchedIcons = mutableSetOf<String>()

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
                prefetchIcons(trophies)
            }
        }
    }

    /**
     * PSN CDN 首字节延迟高,奖杯列表加载后按显示顺序限流预取图标(去重后通常远少于奖杯数)。
     *
     * 跳过首屏可见的前几项:它们由 AsyncImage 自己发起,Coil 没有进行中请求去重,
     * 预取同一张只会双份下载,把首屏最该快的图标挤到连接队列后面。
     */
    private fun prefetchIcons(trophies: List<Trophy>) {
        val urls = trophies.asSequence()
            .drop(FIRST_SCREEN_ICONS)
            .mapNotNull { PsnImageUrl.sized(it.iconUrl, PsnImages.TROPHY_ICON_PX) }
            .distinct()
            .filter { prefetchedIcons.add(it) }
            .toList()
        if (urls.isEmpty()) return
        PsnImages.prefetch(viewModelScope, context, urls, PsnImages.TROPHY_ICON_PX, PsnImages.TROPHY_PARALLELISM)
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

    private companion object {
        /** 首屏可见的奖杯数,这些图标交给 AsyncImage 自己请求,不重复预取。 */
        const val FIRST_SCREEN_ICONS = 8
    }
}
