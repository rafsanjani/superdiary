package com.foreverrafs.superdiary.ui.feature.profile.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.superdiary.ui.components.SuperdiaryImage
import com.foreverrafs.superdiary.ui.feature.profile.ProfileScreenViewData
import com.foreverrafs.superdiary.ui.feature.profile.ProfileScreenViewModel
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen() {
    val viewModel: ProfileScreenViewModel = koinViewModel()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ProfileScreenContent(
        viewState,
    )
}

@Composable
fun ProfileScreenContent(
    viewState: ProfileScreenViewData,
    modifier: Modifier = Modifier,
) {
    SuperdiaryTheme {
        Scaffold(
            modifier = modifier,
            contentColor = MaterialTheme.colorScheme.background,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    SuperdiaryImage(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        url = viewState.avatarUrl,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = viewState.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = viewState.email,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {}) {
                        Text("Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "Preferences",
                        modifier = Modifier.align(Alignment.Start),
                        style = MaterialTheme.typography.labelSmall,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .border(
                                width = 1.dp,
                                color = DividerDefaults.color,
                                shape = RoundedCornerShape(16.dp),
                            ),
                    ) {
                        ProfileItem(
                            label = "Dark Mode",
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                        )

                        ProfileItem(
                            label = "Pin Code",
                            leadingIcon = rememberVectorPainter(Icons.Default.Dialpad),
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                        )

                        SwitchProfileItem(
                            label = "Push Notifications",
                            leadingIcon = rememberVectorPainter(
                                Icons.Default.Notifications,
                            ),
                            checked = true,
                            onCheckChange = {},
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                        )

                        ProfileItem(
                            label = "Logout",
                            labelColor = MaterialTheme.colorScheme.error,
                            leadingIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Logout),
                            leadingIconTint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileItem(
    label: String,
    labelColor: Color = MaterialTheme.colorScheme.onBackground,
    leadingIcon: Painter? = null,
    leadingIconTint: Color = LocalContentColor.current,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = leadingIconTint,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = labelColor,
            )
        }
    }
}

@Composable
private fun SwitchProfileItem(
    label: String,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    labelColor: Color = MaterialTheme.colorScheme.onBackground,
    leadingIcon: Painter? = null,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = labelColor,
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckChange,
        )
    }
}
