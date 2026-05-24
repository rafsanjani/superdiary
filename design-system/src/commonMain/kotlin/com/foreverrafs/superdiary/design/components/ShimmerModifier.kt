package com.foreverrafs.superdiary.design.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.max

@Composable
fun Modifier.shimmer(
    enabled: Boolean = true,
    durationMillis: Int = 1200,
    widthRatio: Float = 0.45f,
): Modifier {
    if (!enabled) return this

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_progress",
    )

    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val shimmerColors = remember(surfaceVariant) {
        listOf(
            surfaceVariant.copy(alpha = 0.35f),
            Color.White.copy(alpha = 0.55f),
            surfaceVariant.copy(alpha = 0.35f),
        )
    }

    return this
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
        .drawWithContent {
            drawContent()

            val shimmerWidth = max(size.width, size.height) * widthRatio
            val travelDistance = size.width + shimmerWidth * 2
            val startX = -shimmerWidth + travelDistance * progress.value

            drawRect(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(startX, 0f),
                    end = Offset(startX + shimmerWidth, size.height),
                ),
                blendMode = BlendMode.SrcAtop,
            )
        }
}
