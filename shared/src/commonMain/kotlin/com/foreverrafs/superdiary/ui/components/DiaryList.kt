package com.foreverrafs.superdiary.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.groupByDate
import com.foreverrafs.superdiary.ui.format
import com.foreverrafs.superdiary.ui.montserratAlternativesFontFamily
import com.foreverrafs.superdiary.ui.screens.DiaryScreenState
import kotlinx.datetime.LocalDate

@Composable
fun DiaryListScreen(
    state: DiaryScreenState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is DiaryScreenState.Content -> DiaryList(
                modifier = Modifier.fillMaxSize(),
                diaries = state.diaries,
            )

            is DiaryScreenState.Error -> Error(modifier = Modifier.fillMaxSize())

            is DiaryScreenState.Loading -> Loading(modifier = Modifier.wrapContentSize())
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CircularProgressIndicator()

        Text(
            text = "Loading Diaries",
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DiaryList(modifier: Modifier = Modifier, diaries: List<Diary>) {
    val groupedDiaries = remember(diaries) {
        diaries.groupByDate()
    }

    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .padding(vertical = 8.dp),
    ) {
        DockedSearchBar(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .align(Alignment.TopCenter),
            query = text,
            onQueryChange = { text = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Hinted search text") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
            colors = SearchBarDefaults.colors(
                containerColor = Color.DarkGray,
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(4) { idx ->
                    val resultText = "Suggestion $idx"

                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = { Text("Additional info") },
                        leadingContent = {
                            Icon(
                                Icons.Filled.LibraryBooks,
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier.clickable {
                            text = resultText
                            active = false
                        },
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = rememberLazyListState(),
        ) {
            groupedDiaries.forEach { (date, diaries) ->
                stickyHeader(key = date.label) {
                    DiaryHeader(
                        text = date.label,
                    )
                }

                items(
                    items = diaries,
                    key = { item -> item.id.toString() },
                ) { diary ->
                    DiaryItem(
                        diary = diary,
                    )
                }
            }
        }
    }
}

@Composable
private fun Error(modifier: Modifier) {
    Text(text = "Error loading diaries", modifier = modifier)
}

@Composable
private fun DiaryHeader(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
private fun DiaryItem(
    modifier: Modifier = Modifier,
    diary: Diary,
) {
    val defaultTextLines = 4

    // Used to determine whether to expand/collapse the card onClick
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var maxLines by remember {
        mutableStateOf(defaultTextLines)
    }

    Card(
        modifier = modifier
            .height(120.dp)
            .animateContentSize(animationSpec = tween(easing = LinearEasing))
            .fillMaxWidth()
            .clickable {
                maxLines = if (!isExpanded) Int.MAX_VALUE else defaultTextLines
                isExpanded = !isExpanded
            },
        shape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 12.dp,
            topEnd = 12.dp,
            bottomEnd = 0.dp,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 12.dp,
                            bottomStart = 12.dp,
                            bottomEnd = 0.dp,
                        ),
                    )
                    .padding(horizontal = 25.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = buildAnnotatedString {
                        val date = LocalDate.parse(diary.date)

                        withStyle(
                            SpanStyle(
                                fontFamily = montserratAlternativesFontFamily(),
                            ),
                        ) {
                            append(
                                date.format("E")
                                    .uppercase(),
                            )
                        }

                        appendLine()

                        withStyle(
                            SpanStyle(
                                fontFamily = montserratAlternativesFontFamily(),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                            ),
                        ) {
                            append(date.dayOfMonth.toString())
                        }
                        appendLine()

                        withStyle(
                            SpanStyle(
                                fontFamily = montserratAlternativesFontFamily(),
                                fontWeight = FontWeight.Normal,
                            ),
                        ) {
                            append(
                                date.format("MMM")
                                    .uppercase(),
                            )
                        }
                        appendLine()

                        withStyle(
                            SpanStyle(
                                fontFamily = montserratAlternativesFontFamily(),
                                fontWeight = FontWeight.Normal,
                            ),
                        ) {
                            append(date.year.toString())
                        }

                        toAnnotatedString()
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
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
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
            )
        }
    }
}
