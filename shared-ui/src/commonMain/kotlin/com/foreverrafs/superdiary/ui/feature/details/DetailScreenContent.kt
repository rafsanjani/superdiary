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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.format
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.label_diary_deleted

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DetailScreenContent(
    diary: Diary,
    onDeleteDiary: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    val richTextState = rememberRichTextState().apply {
        setHtml(diary.entry)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val hostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            SuperDiaryAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                    ) {
                        Icon(
                            modifier = Modifier.clip(CircleShape),
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "",
                        )
                    }
                },
                tralingIcon = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                    ) {
                        Icon(
                            modifier = Modifier.clip(CircleShape),
                            imageVector = Icons.Default.Delete,
                            contentDescription = "",
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState)
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = diary.date
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .format("EEE - MMMM dd, yyyy - hh:mm a")
                        .lowercase(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.6f),
                )

                Spacer(modifier = Modifier.height(20.dp))

                RichText(
                    state = richTextState,
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 32.sp,
                )
            }

            if (showDeleteDialog) {
                val deletedString = stringResource(Res.string.label_diary_deleted)

                ConfirmDeleteDialog(
                    onDismiss = { showDeleteDialog = !showDeleteDialog },
                    onConfirm = {
                        showDeleteDialog = !showDeleteDialog
                        onDeleteDiary()
                        coroutineScope.launch {
                            // Only show snackbar for 600 milliseconds
                            withTimeoutOrNull(600) {
                                hostState.showSnackbar(
                                    message = deletedString,
                                    duration = SnackbarDuration.Indefinite,
                                )
                            }

                            onNavigateBack()
                        }
                    },
                )
            }
        }
    }
}
