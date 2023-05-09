package com.foreverrafs.superdiary.android.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.android.AppTheme
import com.foreverrafs.superdiary.android.style.robotoCondensed
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.groupByDate
import com.ramcosta.composedestinations.annotation.Destination
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.random.Random

@AppNavGraph(start = true)
@Destination
@Composable
fun DiaryListScreen() {
    Content(
        (0..30).map {
            Diary(
                id = Random.nextLong(),
                entry = "Diary Entry #100",
                date = Instant.now().minus(it.toLong() * 2, ChronoUnit.DAYS).toString()
            )
        }
    )
}

@Composable
private fun Content(diaries: List<Diary>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Super Diary",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SearchField(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                DiaryList(diaries)
            }
        }
    }
}

@Composable
private fun DiaryList(diaries: List<Diary>) {
    val groupedDiaries = remember(diaries) {
        diaries.groupByDate()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        groupedDiaries.forEach { (date, diaries) ->
            stickyHeader(key = date) {
                StickyHeader(
                    text = date, modifier = Modifier
                        .fillMaxWidth()
                )
            }

            items(items = diaries, key = { it.id.toString() }) { diary ->
                DiaryCard(diary = diary)
            }
        }
    }
}

@Composable
private fun StickyHeader(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background),
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
) {
    TextField(
        leadingIcon = {
            Icon(imageVector = Icons.Default.Menu, contentDescription = null)
        },
        trailingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        placeholder = {
            Text(
                text = "Search diary by phrase or date...",
                textAlign = TextAlign.Center
            )
        },
        value = "",
        onValueChange = {},
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun DiaryCard(
    modifier: Modifier = Modifier,
    diary: Diary
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        colors = CardDefaults.cardColors(),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 12.dp,
            topEnd = 12.dp,
            bottomEnd = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
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
                            bottomEnd = 0.dp
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = buildAnnotatedString {
                        val letterSpacing = (-0.4).sp
                        val date = ZonedDateTime.parse(diary.date)

                        withStyle(
                            SpanStyle(
                                fontFamily = robotoCondensed,
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
                                fontFamily = robotoCondensed,
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
                                fontFamily = robotoCondensed,
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
                                fontFamily = robotoCondensed,
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

            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.Top),
                text = diary.entry,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = (-0.3).sp
            )
        }
    }
}

@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Day Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            Content(
                diaries = (0..30).map {
                    Diary(
                        id = Random.nextLong(),
                        entry = "Test Diary",
                        date = Instant.now().minus(it.toLong(), ChronoUnit.DAYS).toString()
                    )
                }
            )
        }
    }
}
