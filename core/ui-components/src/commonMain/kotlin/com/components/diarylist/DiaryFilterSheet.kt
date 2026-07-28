package com.components.diarylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.design.components.HeadlineMediumText
import com.foreverrafs.superdiary.design.components.LabelLargeText
import com.foreverrafs.superdiary.design.components.LabelSmallText
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import superdiary.core.ui_components.generated.resources.Res
import superdiary.core.ui_components.generated.resources.apply_button
import superdiary.core.ui_components.generated.resources.cancel_button
import superdiary.core.ui_components.generated.resources.confirm_button
import superdiary.core.ui_components.generated.resources.date_label
import superdiary.core.ui_components.generated.resources.filter_label
import superdiary.core.ui_components.generated.resources.reset_all_button
import superdiary.core.ui_components.generated.resources.select_date_label
import superdiary.core.ui_components.generated.resources.selected_date_label
import superdiary.core.ui_components.generated.resources.sort_and_filter_title
import superdiary.core.ui_components.generated.resources.sort_label
import superdiary.core.ui_components.generated.resources.words_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFilterSheet(
    diaryFilters: DiaryFilters,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onApplyFilters: (filters: DiaryFilters) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        ),
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) {
        var selectedDate by remember {
            mutableStateOf(
                diaryFilters.date,
            )
        }

        var sortByWords by remember {
            mutableStateOf(diaryFilters.sort == DiarySortCriteria.Words)
        }

        var sortByDate by remember {
            mutableStateOf(diaryFilters.sort == DiarySortCriteria.Date)
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(
                    bottom = WindowInsets
                        .systemBars
                        .asPaddingValues()
                        .calculateBottomPadding(),
                ),
        ) {
            HeadlineMediumText(
                text = stringResource(Res.string.sort_and_filter_title),
            )

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            LabelSmallText(
                text = stringResource(Res.string.sort_label),
                fontWeight = FontWeight.Bold,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                DiaryFilterChip(
                    label = stringResource(Res.string.date_label),
                    onSelectionChange = { selected ->
                        sortByDate = selected

                        if (selected) {
                            sortByWords = false
                        }
                    },
                    selected = sortByDate,
                )

                DiaryFilterChip(
                    label = stringResource(Res.string.words_label),
                    onSelectionChange = { selected ->
                        sortByWords = selected

                        if (selected) {
                            sortByDate = false
                        }
                    },
                    selected = sortByWords,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            LabelSmallText(
                text = stringResource(Res.string.filter_label),
                fontWeight = FontWeight.Bold,
            )

            var showDatePickerDialog by remember {
                mutableStateOf(false)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (showDatePickerDialog) {
                    DiaryDatePicker(
                        onDismissRequest = { showDatePickerDialog = false },
                        onDateSelect = {
                            selectedDate = it
                        },
                        selectedDate = selectedDate,
                    )
                }

                TextButton(
                    onClick = {
                        showDatePickerDialog = true
                    },
                ) {
                    LabelLargeText(
                        stringResource(
                            Res.string.selected_date_label,
                            selectedDate?.toString()
                                ?: stringResource(Res.string.select_date_label),
                        ),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val count = listOf(
                    sortByWords,
                    sortByDate,
                ).count { it }
                    .plus(if (selectedDate != null) 1 else 0)

                OutlinedButton(
                    onClick = {
                        selectedDate = null
                        sortByDate = false
                        sortByWords = false
                    },
                    enabled = count != 0,
                ) {
                    if (count != 0) {
                        Badge {
                            LabelSmallText(count.toString())
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    LabelLargeText(stringResource(Res.string.reset_all_button))
                }

                Button(
                    onClick = {
                        onApplyFilters(
                            DiaryFilters(
                                date = selectedDate,
                                sort = if (sortByDate) {
                                    DiarySortCriteria.Date
                                } else if (sortByWords) {
                                    DiarySortCriteria.Words
                                } else {
                                    null
                                },
                            ),
                        )

                        onDismissRequest()
                    },
                ) {
                    LabelLargeText(stringResource(Res.string.apply_button))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryFilterChip(
    label: String,
    onSelectionChange: (selected: Boolean) -> Unit,
    selected: Boolean,
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectionChange(!selected) },
        label = { LabelLargeText(label) },
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = label,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDatePicker(
    onDismissRequest: () -> Unit,
    selectedDate: LocalDate?,
    modifier: Modifier = Modifier,
    onDateSelect: (date: LocalDate) -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            ?.atStartOfDayIn(TimeZone.UTC)
            ?.toEpochMilliseconds(),
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis ->
                        val instant = kotlin.time.Instant.fromEpochMilliseconds(dateMillis)
                        val date = instant.toLocalDateTime(TimeZone.UTC).date

                        onDateSelect(date)
                        onDismissRequest()
                    }
                },
                enabled = true,
            ) {
                LabelLargeText(stringResource(Res.string.confirm_button))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                LabelLargeText(
                    text = stringResource(Res.string.cancel_button),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        modifier = modifier,
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                dayContentColor = MaterialTheme.colorScheme.onBackground,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )
    }
}
