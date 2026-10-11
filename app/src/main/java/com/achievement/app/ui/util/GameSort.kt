package com.achievement.app.ui.util

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.ui.theme.PSNBlue
import com.achievement.app.ui.theme.PSNOnSurface

enum class GameSort(val label: String) {
    Default("默认"),
    Playtime("游玩时长"),
    LastPlayed("最近游玩"),
    Progress("奖杯进度")
}

fun List<Pair<Game, Achievement?>>.applySort(sort: GameSort): List<Pair<Game, Achievement?>> = when (sort) {
    GameSort.Default -> this
    GameSort.Playtime -> sortedByDescending { it.first.playtimeMinutes }
    GameSort.LastPlayed -> sortedByDescending { it.first.lastPlayed ?: "" }
    GameSort.Progress -> sortedByDescending { it.second?.progressPercent ?: -1 }
}

@Composable
fun GameSortSelector(
    selected: GameSort,
    onSelect: (GameSort) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GameSort.entries.forEach { sort ->
            FilterChip(
                selected = sort == selected,
                onClick = { onSelect(sort) },
                label = { Text(sort.label, maxLines = 1) },
                shape = MaterialTheme.shapes.small,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PSNBlue.copy(alpha = 0.18f),
                    selectedLabelColor = PSNBlue,
                    labelColor = PSNOnSurface
                )
            )
        }
    }
}
