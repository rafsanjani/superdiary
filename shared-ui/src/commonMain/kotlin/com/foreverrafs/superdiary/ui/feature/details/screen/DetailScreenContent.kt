package com.foreverrafs.superdiary.ui.feature.details.screen

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
import com.foreverrafs.superdiary.common.utils.format
import com.foreverrafs.superdiary.design.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.design.components.MapComponent
import com.foreverrafs.superdiary.design.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewState
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.label_diary_deleted

@OptIn(ExperimentalRichTextApi::class)
@Composable
fun DetailScreenContent(
    onDeleteDiary: (diary: Diary) -> Unit,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit,
    viewState: DetailsViewState.DiarySelected,
    modifier: Modifier = Modifier,
) {
    val diary = viewState.diary

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
                avatarUrl = viewState.avatarUrl,
                onProfileClick = onProfileClick,
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!diary.location.isEmpty()) {
                    MapComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        latitude = diary.location.latitude,
                        longitude = diary.location.longitude,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = diary.date
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .format("EEE - MMMM dd, yyyy - hh:mm a")
                        .lowercase(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .alpha(0.6f)
                        .padding(12.dp),
                )

                RichText(
                    state = richTextState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
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
                        onDeleteDiary(diary)

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
