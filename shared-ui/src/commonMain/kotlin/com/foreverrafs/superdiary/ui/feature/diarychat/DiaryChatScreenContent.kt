package com.foreverrafs.superdiary.ui.feature.diarychat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.diary.diaryai.DiaryChatMessage
import com.foreverrafs.superdiary.diary.diaryai.DiaryChatRole
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
fun Modifier.positionAwareImePadding() = composed {
    var bottomPadding by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    onPlaced { coordinates ->
        val rootCoordinate = coordinates.findRootCoordinates()
        val bottom = coordinates.positionInWindow().y + coordinates.size.height

        bottomPadding = (rootCoordinate.size.height - bottom).toInt()
    }
        .consumeWindowInsets(PaddingValues(bottom = with(density) { bottomPadding.toDp() }))
        .imePadding()
}

@Composable
fun DiaryChatScreenContent(
    screenState: DiaryChatViewModel.DiaryChatViewState,
    modifier: Modifier = Modifier,
    onQueryDiaries: (query: String) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .positionAwareImePadding()
            .padding(8.dp),
    ) {
        val listState = rememberLazyListState()
        val isKeyboardOpen by keyboardAsState()

        LaunchedEffect(isKeyboardOpen) {
            if (isKeyboardOpen) {
                delay(200)
                listState.animateScrollToItem(screenState.messages.size)
            }
        }

        LaunchedEffect(screenState.messages) {
            listState.animateScrollToItem(screenState.messages.size)
        }

        val renderableListItems =
            remember(screenState.messages) { screenState.messages.filterNot { it.role == DiaryChatRole.System } }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = listState,
        ) {
            items(
                items = renderableListItems,
                key = { item -> item.id },
            ) { item ->
                ChatBubble(
                    chatItem = item,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (screenState.isResponding) {
                item {
                    val alphaAnimation = rememberInfiniteTransition("alphaAnimation")
                    val alpha by alphaAnimation.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(1000),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    )
                    ChatBubble(
                        modifier = modifier.alpha(alpha),
                        chatItem = DiaryChatMessage(
                            id = Random.nextLong(),
                            role = DiaryChatRole.DiaryAI,
                            timestamp = Clock.System.now(),
                            content = "Gathering thoughts..."
                        )
                    )
                }
            }
        }

        var input by remember { mutableStateOf("") }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                value = input,
                onValueChange = { input = it },
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                enabled = input.isNotEmpty(),
                modifier = Modifier.size(48.dp),
                onClick = {
                    onQueryDiaries(input)
                    input = ""
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF008080),
                    contentColor = Color.White,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun ChatLoading() {
    val alphaAnimation = rememberInfiniteTransition("alphaAnimation")
    val alpha by alphaAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1000),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Text(
        text = "Thinking...",
        modifier = Modifier.fillMaxWidth().alpha(alpha),
        textAlign = TextAlign.Center,
    )
}

@Composable
fun ChatBubble(
    chatItem: DiaryChatMessage,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when (chatItem.role) {
        DiaryChatRole.DiaryAI -> Color(0xFF0047AB)
        DiaryChatRole.User -> Color(0xFF008080)
        DiaryChatRole.System -> throw IllegalArgumentException("System chat messages should not be rendered")
    }

    Box(modifier = modifier) {
        val alignmentAndPaddingModifier = when (chatItem.role) {
            DiaryChatRole.DiaryAI -> Modifier.padding(end = 44.dp)
                .align(Alignment.CenterStart)

            DiaryChatRole.User -> Modifier.padding(start = 44.dp)
                .align(Alignment.CenterEnd)

            DiaryChatRole.System -> throw IllegalArgumentException("System chat messages should not be rendered")
        }

        Text(
            text = chatItem.content,
            modifier = alignmentAndPaddingModifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(8.dp),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp, lineHeight = 24.sp),
            color = Color.White,
        )
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
