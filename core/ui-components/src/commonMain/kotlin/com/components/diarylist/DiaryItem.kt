package com.components.diarylist

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.foreverrafs.superdiary.common.utils.format
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.utils.toDate
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material.RichText
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

private enum class Anchors {
    Start,
    End,
}

@OptIn(ExperimentalRichTextApi::class)
@Composable
fun DiaryItem(
    diary: Diary,
    selected: Boolean,
    inSelectionMode: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = updateTransition(selected, label = "selected")
    val padding by transition.animateDp(label = "padding") { _ ->
        if (inSelectionMode) 4.dp else 0.dp
    }

    var draggableWidth by remember { mutableFloatStateOf(0f) }

    val state = rememberSaveable(saver = AnchoredDraggableState.Saver()) {
        AnchoredDraggableState(
            initialValue = Anchors.Start,
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged {
                draggableWidth = it.width.toFloat()
                state.updateAnchors(
                    DraggableAnchors {
                        Anchors.Start at 0f
                        Anchors.End at -(draggableWidth * 0.25f)
                    },
                )
            }
            .anchoredDraggable(
                state = state,
                flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                    state = state,
                    positionalThreshold = { distance: Float -> distance * 0.25f },
                ),
                orientation = Orientation.Horizontal,
                overscrollEffect = rememberOverscrollEffect(),
            )
            .height(110.dp)
            .padding(padding),
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 8.dp,
                topEnd = 8.dp,
                bottomEnd = 0.dp,
            ),
            modifier = Modifier
                .zIndex(.9f)
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        // Workaround for offset getting read before being initialized bug in anchoreddraggable
                        x = if (state.offset.isNaN()) {
                            0
                        } else {
                            state.offset.roundToInt()
                        },
                        y = 0,
                    )
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.semantics {
                    stateDescription = if
                        (diary.isFavorite) {
                        "Favorite"
                    } else {
                        "Not favorite"
                    }

                    customActions = listOf(
                        CustomAccessibilityAction(
                            label = "Toggle Favorite",
                            action = {
                                onToggleFavorite()
                                true
                            },
                        ),
                    )
                }.fillMaxSize(),
            ) {
                DateCard(
                    modifier = Modifier.weight(2.3f),
                    date = diary.date.toDate(),
                )

                val state = rememberRichTextState()
                LaunchedEffect(Unit) {
                    state.setHtml(diary.entry)
                }

                // Diary Entry
                RichText(
                    modifier = Modifier
                        .weight(8f)
                        .clearAndSetSemantics { }
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp,
                            top = 16.dp,
                        )
                        .align(Alignment.Top),
                    letterSpacing = (-0.3).sp,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    state = state,
                )
            }
        }
        // Selection mode icon
        if (inSelectionMode) {
            val iconModifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(top = 12.dp, start = 4.dp).size(20.dp)

            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.RadioButtonUnchecked,
                    tint = Color.White.copy(alpha = 0.7f),
                    contentDescription = null,
                    modifier = iconModifier,
                )
            }
        }

        val totalSize = with(LocalDensity.current) {
            (draggableWidth * 0.25f).toDp()
        }

        val coroutineScope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .size(totalSize)
                .zIndex(.1f)
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = {
                    onToggleFavorite()
                    coroutineScope.launch {
                        // don't reverse the animation straight away
                        delay(250)
                        state.animateTo(Anchors.Start)
                    }
                },
            ) {
                Icon(
                    imageVector = if (diary.isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
internal fun buildDateAnnotatedString(date: LocalDate): AnnotatedString =
    buildAnnotatedString {
        append(
            date.format("E").uppercase(),
        )

        appendLine()

        withStyle(
            SpanStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
            ),
        ) {
            append(date.dayOfMonth.toString())
        }
        appendLine()

        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
        ) {
            append(
                date.format("MMM").uppercase(),
            )
        }
        appendLine()

        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
        ) {
            append(date.year.toString())
        }
    }
