package com.foreverrafs.superdiary.ui.feature.diarylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDiariesSheet(
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false,
        ),
        windowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        var selectedDate by remember {
            mutableStateOf<LocalDate?>(
                null,
            )
        }

        var sortByWords by remember {
            mutableStateOf(false)
        }

        var sortByDate by remember {
            mutableStateOf(false)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sort and Filter",
                style = MaterialTheme.typography.headlineMedium,
            )
            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sort",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                DiaryFilterChip(
                    label = "Date",
                    onSelectionChange = { selected ->
                        sortByDate = selected

                        if (selected) {
                            sortByWords = false
                        }
                    },
                    selected = sortByDate,
                )

                DiaryFilterChip(
                    label = "Words",
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

            Text(
                text = "Filter",
                style = MaterialTheme.typography.labelSmall,
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
                        onDateSelected = {
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
                    Text("Date: ${selectedDate ?: "Select Date"}")
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
                            Text(count.toString())
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text("Reset All")
                }

                Button(onClick = {}) {
                    Text("Apply")
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
        label = { Text(label) },
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
private fun DiaryDatePicker(
    onDismissRequest: () -> Unit,
    onDateSelected: (date: LocalDate) -> Unit,
    selectedDate: LocalDate?,
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
                        val instant = Instant.fromEpochMilliseconds(dateMillis)
                        val date = instant.toLocalDateTime(TimeZone.UTC).date

                        onDateSelected(date)
                        onDismissRequest()
                    }
                },
                enabled = true,
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        DatePicker(
            state = datePickerState,
        )
    }
}
