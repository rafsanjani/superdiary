package com.foreverrafs.superdiary.auth.login.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.superdiary.auth.login.BiometricLoginScreenState
import com.foreverrafs.superdiary.auth.login.BiometricLoginScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BiometricLoginScreen(
    onBiometricAuthSuccess: () -> Unit,
) {
    val viewModel: BiometricLoginScreenViewModel = koinViewModel()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val showBiometricAuthErrorDialog by remember(viewState) {
        mutableStateOf(viewState is BiometricLoginScreenState.Error)
    }

    BiometricLoginScreenContent(
        viewState = viewState,
        onBiometricAuthSuccess = onBiometricAuthSuccess,
        showBiometricAuthErrorDialog = showBiometricAuthErrorDialog,
    )
}

@Composable
fun BiometricLoginScreenContent(
    viewState: BiometricLoginScreenState,
    showBiometricAuthErrorDialog: Boolean,
    onBiometricAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnAuthSuccess by rememberUpdatedState(onBiometricAuthSuccess)

    LaunchedEffect(viewState) {
        when (viewState) {
            is BiometricLoginScreenState.Error -> {}
            is BiometricLoginScreenState.Idle -> {}
            is BiometricLoginScreenState.Success -> currentOnAuthSuccess()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        if (showBiometricAuthErrorDialog) {
            BiometricAuthErrorDialog(
                onExitApp = {},
                onDismissRequest = {
                    // don't allow user to dismiss this dialog
                },
                onTryAgain = {},
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Sign in with biometrics",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Authenticate using your device biometrics",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
