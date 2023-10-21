package com.foreverrafs.superdiary.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm deletion",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete?",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Confirm",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    )
}
