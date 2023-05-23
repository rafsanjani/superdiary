package com.foreverrafs.superdiary.android.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.android.style.sourceSansPro
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.groupByDate
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DiaryList(
    diaries: List<Diary>,
    modifier: Modifier = Modifier,
) {
    val groupedDiaries = remember(diaries) {
        diaries
            .groupByDate()
            .toSortedMap { item1, item2 ->
                item1.priority - item2.priority
            }
    }

    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState
    ) {
        groupedDiaries.forEach { (date, diaries) ->
            stickyHeader(key = date.label) {
                StickyHeader(
                    text = date.label,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .fillMaxWidth(),
                )
            }

            items(
                items = diaries,
                key = { item -> item.id.toString() }
            ) { diary ->
                DiaryCard(
                    diary = diary,
                )
            }
        }
    }
}

@Composable
private fun StickyHeader(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        modifier = modifier.background(color = MaterialTheme.colorScheme.background),
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun DiaryCard(
    modifier: Modifier = Modifier,
    diary: Diary,
) {
    val defaultTextLines = 4

    // Used to determine whether to expand/collapse the card onClick
    var isOverFlowing by remember {
        mutableStateOf(false)
    }

    var maxLines by remember {
        mutableStateOf(defaultTextLines)
    }

    Card(
        modifier = modifier
            .animateContentSize(animationSpec = tween(easing = LinearEasing))
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
            .clickable {
                maxLines = if (isOverFlowing) Int.MAX_VALUE else defaultTextLines
            },
        shape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 12.dp,
            topEnd = 12.dp,
            bottomEnd = 0.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(
                            topStart = 0.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 0.dp
                        )
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    text = buildAnnotatedString {
                        val letterSpacing = (-0.4).sp
                        val date = LocalDate.parse(diary.date)

                        withStyle(
                            SpanStyle(
                                fontFamily = sourceSansPro,
                                letterSpacing = letterSpacing,
                            )
                        ) {
                            append(
                                date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ROOT)
                                    .uppercase()
                            )
                        }

                        appendLine()

                        withStyle(
                            SpanStyle(
                                fontFamily = sourceSansPro,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = letterSpacing,
                                fontSize = 20.sp,
                            )
                        ) {
                            append(date.dayOfMonth.toString())
                        }
                        appendLine()

                        withStyle(
                            SpanStyle(
                                fontFamily = sourceSansPro,
                                letterSpacing = letterSpacing,
                            )
                        ) {
                            append(
                                date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                                    .uppercase()
                            )
                        }
                        appendLine()

                        withStyle(
                            SpanStyle(
                                fontFamily = sourceSansPro,
                                letterSpacing = letterSpacing,
                            )
                        ) {
                            append(date.year.toString())
                        }

                        toAnnotatedString()
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // Diary Entry
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Top),
                text = diary.entry,
                letterSpacing = (-0.3).sp,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    isOverFlowing =
                        textLayoutResult.didOverflowHeight || textLayoutResult.didOverflowWidth
                },
                textAlign = TextAlign.Justify
            )
        }
    }
}
