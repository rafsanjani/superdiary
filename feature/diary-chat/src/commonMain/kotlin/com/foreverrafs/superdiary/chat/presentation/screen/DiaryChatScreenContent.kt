package com.foreverrafs.superdiary.chat.presentation.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatMessage
import com.foreverrafs.superdiary.ai.domain.model.DiaryChatRole
import com.foreverrafs.superdiary.chat.presentation.DiaryChatUiModel
import com.foreverrafs.superdiary.chat.presentation.DiaryChatViewState
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_chat.generated.resources.Res
import superdiary.feature.diary_chat.generated.resources.content_description_button_send
import superdiary.feature.diary_chat.generated.resources.ic_send

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DiaryChatScreenContent(
    screenState: DiaryChatViewState,
    modifier: Modifier = Modifier,
    onQueryDiaries: (query: String) -> Unit = {},
) {
    when (screenState) {
        is DiaryChatViewState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading Diaries")
            }
        }

        is DiaryChatViewState.Initialized -> {
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current

            Column(
                modifier = modifier
                    .clickable(
                        indication = null,
                        onClick = {
                            keyboardController?.hide()
                        },
                        interactionSource = MutableInteractionSource(),
                    )
                    .fillMaxSize()
                    .imePadding()
                    .padding(8.dp),
            ) {
                val listState = rememberLazyListState()

                LaunchedEffect(screenState.model.messages) {
                    listState.animateScrollToItem(screenState.model.messages.size)
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    state = listState,
                ) {
                    items(
                        items = screenState.model.messages,
                        key = { item -> item.id },
                    ) { item ->
                        ChatBubble(
                            chatItem = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                        )
                    }

                    if (screenState.model.isResponding) {
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
                                modifier = Modifier.alpha(alpha),
                                chatItem = DiaryChatMessage(
                                    id = Uuid.random().toString(),
                                    role = DiaryChatRole.DiaryAI,
                                    timestamp = Clock.System.now(),
                                    content = "Responding.",
                                ),
                            )
                        }
                    }
                }

                var input by remember { mutableStateOf("") }

                Spacer(modifier = Modifier.height(10.dp))

                // Input area and send button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }

                    // Input area
                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequester).weight(1f)
                            .heightIn(min = 48.dp),
                        maxLines = 1,
                        value = input,
                        onValueChange = { input = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                onQueryDiaries(input)
                                input = ""
                            },
                        ),
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Send button
                    val description = stringResource(Res.string.content_description_button_send)
                    IconButton(
                        enabled = input.isNotEmpty() && !screenState.model.isResponding,
                        modifier = Modifier.size(48.dp).semantics(true) {
                            this.contentDescription = description
                        },
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
                            painter = painterResource(Res.drawable.ic_send),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
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
            DiaryChatRole.DiaryAI -> Modifier.padding(end = 44.dp).align(Alignment.CenterStart)

            DiaryChatRole.User -> Modifier.padding(start = 44.dp).align(Alignment.CenterEnd)

            DiaryChatRole.System -> throw IllegalArgumentException("System chat messages should not be rendered")
        }

        val richTextState = rememberRichTextState()

        LaunchedEffect(chatItem) {
            richTextState.setMarkdown(chatItem.content)
        }

        RichText(
            state = richTextState,
            modifier = alignmentAndPaddingModifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(8.dp),
            style = MaterialTheme.typography.bodySmall.merge(fontSize = 14.sp, lineHeight = 24.sp),
            color = Color.White,
        )
    }
}

@Composable
fun keyboardVisibilityState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Preview
@Composable
private fun PreviewResponding() {
    SuperDiaryPreviewTheme {
        DiaryChatScreenContent(
            screenState = DiaryChatViewState.Initialized(
                DiaryChatUiModel(isResponding = true),
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewMessagesAndResponding() {
    SuperDiaryPreviewTheme {
        DiaryChatScreenContent(
            screenState = DiaryChatViewState.Initialized(
                DiaryChatUiModel(
                    isResponding = true,
                    messages = listOf(
                        DiaryChatMessage.System("You are diary AI"),
                        DiaryChatMessage.User("You don't kill a cat, a cat kills you"),
                        DiaryChatMessage.DiaryAI("How do i kill a cat"),
                    ),
                ),
            ),
        )
    }
}
