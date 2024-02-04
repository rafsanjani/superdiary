package me.saket.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation.Horizontal
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** A composable that can be swiped left or right for revealing actions. */
@Composable
fun SwipeableActionsBox(
    state: SwipeableActionsState,
    action: SwipeAction,
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit)
) = BoxWithConstraints(modifier) {

    val actions = remember(action) {
        ActionFinder(left = emptyList(), right = listOf(action))
    }

    LaunchedEffect(state, actions) {
        state.run {
            canSwipeTowardsRight = { false }
            canSwipeTowardsLeft = { true }
        }
    }

    val offset = state.offset.value

    var swipedAction: SwipeActionMeta? by remember {
        mutableStateOf(null)
    }
    val visibleAction: SwipeActionMeta? = remember(offset, actions) {
        actions.actionAt(offset, totalWidth = constraints.maxWidth)
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .absoluteOffset { IntOffset(x = offset.roundToInt(), y = 0) }
            .draggable(
                orientation = Horizontal,
                enabled = !state.isResettingOnRelease,
                onDragStopped = {
                    scope.launch {
                        state.snapToEnd()
                    }
                },
                state = state.draggableState,
            ),
        content = content
    )

    (swipedAction ?: visibleAction)?.let { action ->
        ActionIconBox(
            modifier = Modifier
                .matchParentSize()
                .clickable(indication = null, interactionSource = MutableInteractionSource()) {
                    scope.launch {
                        visibleAction?.value?.onClick?.invoke()
                        delay(200)
                        state.resetOffset()
                        swipedAction = null
                    }
                },
            action = action,
            offset = offset,
            backgroundColor = Color.Transparent,
            content = { action.value.icon() }
        )
    }
}

@Composable
private fun ActionIconBox(
    action: SwipeActionMeta,
    offset: Float,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(width = placeable.width, height = placeable.height) {
                    // Align icon with the left/right edge of the content being swiped.
                    val iconOffset = if (action.isOnRightSide) {
                        constraints.maxWidth + offset
                    } else {
                        offset - placeable.width
                    }
                    placeable.placeRelative(x = iconOffset.roundToInt(), y = 0)
                }
            }
            .background(color = backgroundColor),
        horizontalArrangement = if (action.isOnRightSide) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

