package me.saket.swipe

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

data class SwipeAction(
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
)

fun SwipeAction(
    onActionClicked: () -> Unit,
    icon: Painter,
): SwipeAction {
    return SwipeAction(
        icon = {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        onClick = onActionClicked,
    )
}
