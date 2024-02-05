package me.saket.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlin.math.absoluteValue

@Composable
fun rememberSwipeableActionsState(iconWidthPx: Int): SwipeableActionState {
    return remember { SwipeableActionState(iconWidthPx) }
}

/** The state of a [SwipeableActionBox]. */
@Stable
class SwipeableActionState internal constructor(private val iconWidthPx: Int) {
    /** The current position (in pixels) of a [SwipeableActionBox]. */
    val offset: State<Float> get() = offsetState
    private var offsetState = mutableStateOf(0f)

    internal val draggableState = DraggableState { delta ->
        var newDelta = offsetState.value + delta

        if (newDelta.absoluteValue >= iconWidthPx) {
            return@DraggableState
        }

        newDelta = newDelta.coerceAtMost(0f)

        offsetState.value = newDelta
    }

    suspend fun finalizeDrag() {
        if (offset.value == 0f) return

        val targetValue = if (offset.value.absoluteValue >= iconWidthPx / 2) iconWidthPx.toFloat() * -1 else 0f

        dragBy(targetValue = targetValue)
    }

    suspend fun resetDrag() {
        dragBy(targetValue = 0f)
    }

    private suspend fun dragBy(targetValue: Float) {
        draggableState.drag(MutatePriority.PreventUserInput) {
            Animatable(offset.value).animateTo(
                targetValue = targetValue,
                animationSpec = tween(durationMillis = animationDurationMs),
            ) {
                dragBy(value - offsetState.value)
            }
        }
    }
}
