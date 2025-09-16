package com.foreverrafs.superdiary.ui.feature.changepassword.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.design.components.BodyMediumText
import com.foreverrafs.superdiary.design.components.PrimaryButton
import com.foreverrafs.superdiary.design.components.TitleMediumText
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChangePasswordSuccessScreen(
    onPrimaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
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
                imageVector = Icons.Default.CheckCircle,
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
                onClick = onPrimaryButtonClick,
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
            onPrimaryButtonClick = {},
        )
    }
}
