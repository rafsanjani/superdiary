package com.foreverrafs.superdiary.auth.changepassword.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.foreverrafs.superdiary.design.components.BodyMediumText
import com.foreverrafs.superdiary.design.components.PrimaryButton
import com.foreverrafs.superdiary.design.components.TitleMediumText
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.painterResource
import superdiary.feature.diary_auth.generated.resources.Res
import superdiary.feature.diary_auth.generated.resources.ic_check_circle

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChangePasswordSuccessScreen(
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBackHandler(
        isBackEnabled = true,
        state = rememberNavigationEventState(
            currentInfo = NavigationEventInfo.None,
        ),
        onBackCompleted = {
            // Disable back navigation on this screen
        },
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
        ) {
            Spacer(
                modifier = Modifier.weight(0.3f),
            )

            TitleMediumText("Password successfully changed")

            Spacer(
                modifier = Modifier.height(24.dp),
            )

            Icon(
                modifier = Modifier.size(52.dp),
                painter = painterResource(Res.drawable.ic_check_circle),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.height(24.dp))

            BodyMediumText(
                "You’re all set! Please remember to use your new password when logging back into the app",
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Continue",
                onClick = onContinueClick,
                enabled = true,
            )
        }
    }
}

@Composable
@Preview
private fun ChangePasswordSuccessScreenPreview() {
    SuperDiaryPreviewTheme {
        ChangePasswordSuccessScreen(
            onContinueClick = {},
        )
    }
}
