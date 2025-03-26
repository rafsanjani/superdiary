// package com.foreverrafs.superdiary.ui
//
// import android.content.res.Configuration
// import androidx.compose.animation.ExperimentalSharedTransitionApi
// import androidx.compose.foundation.layout.Row
// import androidx.compose.foundation.layout.Spacer
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.layout.width
// import androidx.compose.foundation.selection.selectable
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.RadioButton
// import androidx.compose.material3.Scaffold
// import androidx.compose.material3.Surface
// import androidx.compose.material3.Text
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.semantics.Role
// import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.unit.dp
// import androidx.compose.ui.unit.sp
// import com.foreverrafs.superdiary.auth.login.BiometricLoginScreenState
// import com.foreverrafs.superdiary.auth.login.screen.BiometricLoginScreenContent
// import com.foreverrafs.superdiary.auth.register.screen.RegistrationConfirmationScreen
// import com.foreverrafs.superdiary.design.components.SuperDiaryAppBar
// import com.foreverrafs.superdiary.design.style.SuperDiaryTheme
// import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewData
// import com.foreverrafs.superdiary.profile.presentation.screen.ProfileScreenContent
// import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
// import com.foreverrafs.superdiary.ui.feature.diarychat.screen.DiaryChatScreenContent
// import com.foreverrafs.superdiary.utils.DiarySettings
//
// @OptIn(ExperimentalSharedTransitionApi::class)
// @Composable
// fun TestAppContainer(
//    modifier: Modifier = Modifier,
//    content: @Composable () -> Unit,
// ) {
//    SuperDiaryTheme {
//        Scaffold(
//            modifier = modifier,
//            topBar = {
//                SuperDiaryAppBar()
//            },
//            contentColor = MaterialTheme.colorScheme.background,
//        ) {
//            Surface(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(it),
//            ) {
//                content()
//            }
//        }
//    }
// }
//
// @Composable
// @PreviewSuperDiary
// private fun BiometricLoginScreen() {
//    SuperDiaryTheme {
//        BiometricLoginScreenContent(
//            viewState = BiometricLoginScreenState.Idle,
//            onBiometricAuthSuccess = {},
//            showBiometricAuthErrorDialog = false,
//        )
//    }
// }
//
// @PreviewSuperDiary
// @Composable
// private fun ProfileScreenPreview() {
//    ProfileScreenContent(
//        viewState = ProfileScreenViewData(
//            name = "Rafsanjani Aziz",
//            email = "foreverrafs@gmail.com",
//        ),
//        onConsumeErrorMessage = {},
//        onLogout = {},
//        settings = DiarySettings.Empty,
//        onUpdateSettings = {},
//        onLogoutDialogVisibilityChange = {},
//        isLogoutDialogVisible = false,
//        onNavigateBack = {},
//    )
// }
//
// @Preview
// @Composable
// private fun ProfileScreenPreviewLogoutDialog() {
//    ProfileScreenContent(
//        viewState = ProfileScreenViewData(
//            name = "Rafsanjani Aziz",
//            email = "foreverrafs@gmail.com",
//        ),
//        onConsumeErrorMessage = {},
//        onLogout = {},
//        onLogoutDialogVisibilityChange = {},
//        isLogoutDialogVisible = true,
//        settings = DiarySettings.Empty,
//        onUpdateSettings = {},
//        onNavigateBack = {},
//        animatedContentScope = this,
//        sharedTransitionScope = this,
//    )
//
// }
//
// @Composable
// @PreviewSuperDiary
// private fun RegistrationConfirmationPreview() {
//    SuperDiaryTheme {
//        SuperDiaryTheme {
//            Surface(color = MaterialTheme.colorScheme.background) {
//                RegistrationConfirmationScreen()
//            }
//        }
//    }
// }
//
// @PreviewSuperDiary
// @Composable
// private fun DiaryChatPreview() {
//    TestAppContainer {
//        DiaryChatScreenContent(
//            screenState = DiaryChatViewModel.DiaryChatViewState(
//                isResponding = true,
//            ),
//        )
//    }
// }
//
// @PreviewSuperDiary
// @Composable
// fun Test() {
//    SuperDiaryTheme {
//        Row(
//            modifier = Modifier.selectable(selected = true, onClick = {}, role = Role.RadioButton),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            RadioButton(
//                selected = true,
//                onClick = null,
//                modifier = Modifier.alignByBaseline() // Aligns with the text baseline
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = "Click here",
//                fontSize = 60.sp,
//                modifier = Modifier.alignByBaseline()
//            )
//        }
//    }
// }
//
//
// @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
// @Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
// annotation class PreviewSuperDiary
