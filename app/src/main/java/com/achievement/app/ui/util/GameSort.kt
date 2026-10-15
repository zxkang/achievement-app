package com.achievement.app.ui.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.achievement.app.domain.model.Achievement
import com.achievement.app.domain.model.Game
import com.achievement.app.ui.theme.PSNBlue
import com.achievement.app.ui.theme.PSNBlueLight
import com.achievement.app.ui.theme.PSNOnSurface
import com.achievement.app.ui.theme.PSNOnSurfaceVariant
import com.achievement.app.ui.theme.PSNSurfaceVariant

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
                label = { Text(sort.label, maxLines = 1, style = MaterialTheme.typography.labelMedium) },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.dp,
                    if (sort == selected) PSNBlue.copy(alpha = 0.6f) else PSNSurfaceVariant
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PSNBlue.copy(alpha = 0.16f),
                    selectedLabelColor = PSNBlueLight,
                    labelColor = PSNOnSurfaceVariant
                )
            )
        }
    }
}

/** 单行紧凑排序入口:与搜索框同排使用,点击弹出菜单选择,替代整排筛选芯片以节省高度。 */
@Composable
fun GameSortDropdown(
    selected: GameSort,
    onSelect: (GameSort) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val active = selected != GameSort.Default
    Box(modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(
                1.dp,
                if (active) PSNBlue.copy(alpha = 0.6f) else PSNSurfaceVariant
            ),
            color = if (active) PSNBlue.copy(alpha = 0.16f) else Color.Transparent
        ) {
            Row(
                modifier = Modifier.height(44.dp).padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Sort,
                    contentDescription = null,
                    tint = if (active) PSNBlueLight else PSNOnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = selected.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (active) PSNBlueLight else PSNOnSurfaceVariant,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            GameSort.entries.forEach { sort ->
                DropdownMenuItem(
                    text = { Text(sort.label, color = PSNOnSurface) },
                    leadingIcon = {
                        if (sort == selected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PSNBlueLight)
                        }
                    },
                    onClick = {
                        onSelect(sort)
                        expanded = false
                    }
                )
            }
        }
    }
}
