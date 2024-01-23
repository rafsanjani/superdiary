package com.foreverrafs.superdiary.ui.feature.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.toDate
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.format
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText

@Composable
fun DetailScreenContent(
    onNavigateBack: () -> Unit,
    diary: Diary,
) {
    val richTextState = rememberRichTextState().apply {
        setHtml(diary.entry)
    }

    Scaffold(
        topBar = {
            SuperDiaryAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                    ) {
                        Icon(
                            modifier = Modifier.clip(CircleShape),
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Navigate back",
                        )
                    }
                },
                tralingIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                    ) {
                        Icon(
                            modifier = Modifier.clip(CircleShape),
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Navigate back",
                        )
                    }
                }
            )
        },
    ) {
        Surface(
            modifier = Modifier.padding(it),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = diary.date.toDate().format("EEE - MMMM dd, yyyy"),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.6f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                RichText(
                    state = richTextState,
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 32.sp
                )
            }
        }
    }
}
