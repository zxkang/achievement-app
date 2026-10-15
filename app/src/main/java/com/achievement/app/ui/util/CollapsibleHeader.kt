package com.achievement.app.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 随列表滚动收起/展开的头部:向下滚时头部滑出的同时占位高度同步收缩
 * (列表立即获得腾出的空间,不留空洞),向上滚立即跟手展开。
 *
 * 用法:
 * ```
 * val header = rememberCollapsibleHeaderState()
 * Column(Modifier.nestedScroll(header.connection)) {
 *     CollapsibleHeader(header) { 搜索/筛选内容 }
 *     LazyColumn { ... }
 * }
 * ```
 */
class CollapsibleHeaderState internal constructor(
    val connection: NestedScrollConnection,
    internal val holder: HeaderHolder
)

/** 头部是否已完全收起,供调用方按需调整样式。 */
val CollapsibleHeaderState.isCollapsed: Boolean
    get() = holder.heightPx > 0 && abs(holder.offset) >= holder.heightPx - 1f

/** 立即展开头部(如筛选变化列表回到顶部时,保持顶部区域完整可见)。 */
fun CollapsibleHeaderState.expand() {
    holder.offset = 0f
}

internal class HeaderHolder {
    var offset by mutableFloatStateOf(0f)
    var heightPx by mutableIntStateOf(0)

    fun onScroll(deltaY: Float): Float {
        if (heightPx <= 0) return 0f
        val prev = offset
        offset = (prev + deltaY).coerceIn(-heightPx.toFloat(), 0f)
        return offset - prev
    }
}

@Composable
fun rememberCollapsibleHeaderState(): CollapsibleHeaderState {
    val holder = remember { HeaderHolder() }
    val connection = remember(holder) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.Drag) return Offset.Zero
                return Offset(0f, holder.onScroll(available.y))
            }
        }
    }
    return remember(connection, holder) { CollapsibleHeaderState(connection, holder) }
}

@Composable
fun CollapsibleHeader(
    state: CollapsibleHeaderState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val holder = state.holder
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
            // 占位高度直接以像素计算(勿经 dp 换算,否则展开态会被密度放大数倍留空洞);
            // 内容始终按全高测量,收起只靠裁切+位移,避免压扁内容污染 heightPx 的反馈循环。
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)
                )
                val visible = (holder.heightPx + holder.offset)
                    .roundToInt()
                    .coerceIn(0, placeable.height)
                layout(constraints.maxWidth, visible) {
                    placeable.placeRelative(0, 0)
                }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .onSizeChanged {
                    // 只在拿到有效高度时更新,避免瞬时 0 尺寸污染收起行程
                    if (it.height > 0 && holder.heightPx != it.height) holder.heightPx = it.height
                }
                .graphicsLayer { translationY = holder.offset },
            content = content
        )
    }
}
