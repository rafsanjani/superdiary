package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.creatediary.components.RichTextStyleRow
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDiaryScreenContent(
    onNavigateBack: () -> Unit,
    onSaveDiary: (entry: String) -> Unit,
) {
    Scaffold(
        topBar = {
            SuperDiaryAppBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .clickable {
                                onNavigateBack()
                            }
                            .padding(8.dp),
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                    )
                },
            )
        },
    ) {
        Surface(
            modifier = Modifier.padding(it),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
            ) {
                val richTextState = rememberRichTextState()

                RichTextStyleRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    state = richTextState,
                )

                OutlinedRichTextEditor(
                    state = richTextState,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
