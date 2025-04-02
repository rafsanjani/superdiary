package com.foreverrafs.superdiary.auth.register.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.auth.openDefaultEmailApp
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_auth.generated.resources.Res
import superdiary.feature.diary_auth.generated.resources.label_open_email
import superdiary.feature.diary_auth.generated.resources.registration_email_confirmation_prompt
import superdiary.feature.diary_auth.generated.resources.registration_success_message

@Composable
fun RegistrationConfirmationScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .padding(it)
                .consumeWindowInsets(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(Res.string.registration_success_message),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                modifier = Modifier.size(70.dp),
                imageVector = Icons.Default.MarkEmailUnread,
                contentDescription = null,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.registration_email_confirmation_prompt),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = ::openDefaultEmailApp,
            ) {
                Text(text = stringResource(Res.string.label_open_email))
            }
        }
    }
}
