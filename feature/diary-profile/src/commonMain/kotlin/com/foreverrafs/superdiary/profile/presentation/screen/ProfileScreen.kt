package com.foreverrafs.superdiary.profile.presentation.screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.foreverrafs.superdiary.design.components.BodySmallText
import com.foreverrafs.superdiary.design.components.Image
import com.foreverrafs.superdiary.design.components.LabelLargeText
import com.foreverrafs.superdiary.design.components.LabelSmallText
import com.foreverrafs.superdiary.design.components.PROFILE_IMAGE_SHARED_ELEMENT_KEY
import com.foreverrafs.superdiary.design.components.TitleMediumText
import com.foreverrafs.superdiary.design.style.LocalSharedTransitionScope
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.foreverrafs.superdiary.design.style.SuperDiaryTheme
import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewData
import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewModel
import com.foreverrafs.superdiary.utils.DiarySettings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import superdiary.feature.diary_profile.generated.resources.Res
import superdiary.feature.diary_profile.generated.resources.ic_arrow_back
import superdiary.feature.diary_profile.generated.resources.ic_logout
import superdiary.feature.diary_profile.generated.resources.profile_edit_button
import superdiary.feature.diary_profile.generated.resources.profile_latest_entries_label
import superdiary.feature.diary_profile.generated.resources.profile_navigate_back_content_description
import superdiary.feature.diary_profile.generated.resources.profile_on_this_day_label
import superdiary.feature.diary_profile.generated.resources.profile_screen_daily_reminder_email
import superdiary.feature.diary_profile.generated.resources.profile_screen_daily_reminder_email_description
import superdiary.feature.diary_profile.generated.resources.profile_screen_section_dashboard_cards
import superdiary.feature.diary_profile.generated.resources.profile_screen_section_email_preferences
import superdiary.feature.diary_profile.generated.resources.profile_sign_out_button
import superdiary.feature.diary_profile.generated.resources.profile_sign_out_error
import superdiary.feature.diary_profile.generated.resources.profile_weekly_summary_label
import superdiary.feature.diary_profile.generated.resources.unique_email_address_label

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProfileScreen(
    onLogoutComplete: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val viewModel: ProfileScreenViewModel = koinViewModel()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var isLogoutDialogVisible by remember { mutableStateOf(false) }
    val currentOnLogoutComplete by rememberUpdatedState(onLogoutComplete)
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    LaunchedEffect(viewState) {
        if (viewState.isLogoutSuccess == true) {
            currentOnLogoutComplete()
        }
    }

    ProfileScreenContent(
        viewState = viewState,
        onConsumeErrorMessage = viewModel::resetErrors,
        onLogout = viewModel::onLogout,
        onLogoutDialogVisibilityChange = {
            isLogoutDialogVisible = it
        },
        isLogoutDialogVisible = isLogoutDialogVisible,
        settings = settings,
        onUpdateSettings = viewModel::onSettingsUpdated,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProfileScreenContent(
    viewState: ProfileScreenViewData,
    onConsumeErrorMessage: () -> Unit,
    onLogout: () -> Unit,
    onLogoutDialogVisibilityChange: (Boolean) -> Unit,
    onUpdateSettings: (DiarySettings) -> Unit,
    onNavigateBack: () -> Unit,
    isLogoutDialogVisible: Boolean,
    settings: DiarySettings,
    modifier: Modifier = Modifier,
) {
    val snackBarkHostState = remember { SnackbarHostState() }
    val currentOnConsumeErrorMessage by rememberUpdatedState(onConsumeErrorMessage)
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalNavAnimatedContentScope.current

    val signOutError = stringResource(Res.string.profile_sign_out_error)
    LaunchedEffect(viewState.hasError) {
        if (viewState.hasError) {
            snackBarkHostState.showSnackbar(signOutError)
            currentOnConsumeErrorMessage()
        }
    }

    SuperDiaryTheme {
        Scaffold(
            modifier = modifier,
            snackbarHost = {
                SnackbarHost(snackBarkHostState)
            },
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background,
            ) {
                if (isLogoutDialogVisible) {
                    ConfirmLogoutDialog(
                        onLogout = {
                            onLogout()
                            onLogoutDialogVisibilityChange(false)
                        },
                        onDismiss = {
                            onLogoutDialogVisibilityChange(false)
                        },
                        onDismissRequest = {
                            onLogoutDialogVisibilityChange(false)
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    NavigateBackButton(
                        modifier = Modifier.align(Alignment.Start),
                        onClick = onNavigateBack,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileHeader(
                        viewState = viewState,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope,
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    ProfileSection(
                        label = stringResource(Res.string.profile_screen_section_dashboard_cards),
                    ) {
                        CheckboxProfileItem(
                            label = stringResource(Res.string.profile_weekly_summary_label),
                            checked = settings.showWeeklySummary,
                            onCheckChange = {
                                onUpdateSettings(
                                    settings.copy(
                                        showWeeklySummary = it,
                                    ),
                                )
                            },
                        )

                        CheckboxProfileItem(
                            label = stringResource(Res.string.profile_on_this_day_label),
                            checked = true,
                            onCheckChange = {},
                        )

                        CheckboxProfileItem(
                            label = stringResource(Res.string.profile_latest_entries_label),
                            checked = settings.showLatestEntries,
                            onCheckChange = {
                                onUpdateSettings(
                                    settings.copy(
                                        showLatestEntries = it,
                                    ),
                                )
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileSection(
                        label = stringResource(Res.string.profile_screen_section_email_preferences),
                    ) {
                        CheckboxProfileItem(
                            label = stringResource(Res.string.profile_screen_daily_reminder_email),
                            checked = settings.dailyReminderEmail,
                            onCheckChange = {
                                onUpdateSettings(
                                    settings.copy(
                                        dailyReminderEmail = it,
                                    ),
                                )
                            },
                        )
                        LabelSmallText(
                            text = stringResource(
                                Res.string.profile_screen_daily_reminder_email_description,
                            ),
                            modifier = Modifier
                                .alpha(0.6f)
                                .padding(start = 16.dp, bottom = 12.dp),
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    BodySmallText(
                        text = stringResource(Res.string.unique_email_address_label),
                    )

                    SelectionContainer {
                        LabelSmallText(
                            text = viewState.uniqueEmailAddress,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    TextButton(
                        onClick = {
                            onLogoutDialogVisibilityChange(true)
                        },
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_logout),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.error,
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            BodySmallText(
                                modifier = Modifier.padding(vertical = 8.dp),
                                text = stringResource(Res.string.profile_sign_out_button),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigateBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            modifier = Modifier.clip(CircleShape),
            painter = painterResource(Res.drawable.ic_arrow_back),
            contentDescription = stringResource(
                Res.string.profile_navigate_back_content_description,
            ),
        )
    }
}

@Composable
private fun ProfileHeader(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewState: ProfileScreenViewData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        with(sharedTransitionScope) {
            Image(
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = sharedTransitionScope.rememberSharedContentState(
                            key = PROFILE_IMAGE_SHARED_ELEMENT_KEY,
                        ),
                        animatedVisibilityScope = animatedContentScope,
                    )
                    .size(120.dp)
                    .clip(CircleShape),
                url = viewState.avatarUrl,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        TitleMediumText(
            text = viewState.name,
        )
        Spacer(modifier = Modifier.height(8.dp))

        BodySmallText(
            text = viewState.email,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {}) {
            LabelLargeText(stringResource(Res.string.profile_edit_button))
        }
    }
}

@Composable
private fun ProfileSection(
    label: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        LabelSmallText(
            text = label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp),
            fontWeight = FontWeight.SemiBold,
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
            content = content,
        )
    }
}

@Composable
private fun CheckboxProfileItem(
    label: String,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    labelColor: Color = MaterialTheme.colorScheme.onBackground,
    leadingIcon: Painter? = null,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
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
            BodySmallText(
                text = label,
                color = labelColor,
            )
        }

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckChange,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun Preview() {
    SuperDiaryPreviewTheme {
        ProfileScreenContent(
            viewState = ProfileScreenViewData(
                name = "John Doe",
                email = "foreverrafs@gmail.com",
                uniqueEmailAddress = "S2FZ8rv7U@emailparse.nebulainnova.co.uk",
            ),
            onConsumeErrorMessage = {},
            onLogout = {},
            settings = DiarySettings.Empty,
            onUpdateSettings = {},
            onLogoutDialogVisibilityChange = {},
            isLogoutDialogVisible = false,
            onNavigateBack = {},
        )
    }
}
