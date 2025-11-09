package com.foreverrafs.superdiary.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.painterResource
import superdiary.design_system.generated.resources.Res
import superdiary.design_system.generated.resources.ic_share_location

@Composable
expect fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
)

@Composable
expect fun ConfirmSaveDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
)

@Composable
expect fun ConfirmBiometricAuthDialog(
    onDismiss: () -> Unit,
    onEnableBiometric: () -> Unit,
    onDismissRequest: () -> Unit,
)

@Composable
fun BasicMaterialDialog(
    title: String,
    message: String,
    negativeButtonText: String,
    onNegativeButton: () -> Unit,
    onPositiveButton: () -> Unit,
    positiveButtonText: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    AlertDialog(
        properties = properties,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onPositiveButton) {
                Text(
                    text = positiveButtonText,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onNegativeButton) {
                Text(
                    text = negativeButtonText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationRationaleDialog(
    isPermissionDeniedAlways: Boolean,
    onRequestLocationPermission: () -> Unit,
    onDontAskAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicAlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        onDismissRequest = {},
        content = {
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
                elevation = CardDefaults.elevatedCardElevation(),
            ) {
                Column {
                    Icon(
                        painter = painterResource(Res.drawable.ic_share_location),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 35.dp)
                            .height(70.dp)
                            .fillMaxWidth(),
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = "Location Tags",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        val locationPermissionDialogMessage = if (isPermissionDeniedAlways) {
                            """
                                To use location tags in your entries, you need to enable location permission from your phone's settings menu
                            """.trimIndent()
                        } else {
                            "Allow location permission to use your location to personalise your entries"
                        }

                        Text(
                            text = locationPermissionDialogMessage,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(
                                    top = 10.dp,
                                    start = 25.dp,
                                    end = 25.dp,
                                )
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                    ) {
                        if (isPermissionDeniedAlways) {
                            TextButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("request_location_cancel"),
                                onClick = onDontAskAgain,
                            ) {
                                Text(
                                    text = "Don't ask again",
                                    color = Color.Red,
                                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                )
                            }
                        }

                        TextButton(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("request_location_proceed"),
                            onClick = onRequestLocationPermission,
                        ) {
                            Text(
                                text = "Proceed",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                            )
                        }
                    }
                }
            }
        },
    )
}
