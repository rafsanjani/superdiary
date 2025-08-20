package com.foreverrafs.superdiary.ui.feature.changepassword.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.superdiary.ui.feature.changepassword.ChangePasswordViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangePasswordScreen(
    onPasswordChangeSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ChangePasswordViewModel = koinViewModel()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    ChangePasswordScreenContent(
        modifier = modifier,
        onAction = viewModel::onAction,
        viewState = viewState,
        onPasswordChangeSuccess = onPasswordChangeSuccess,
        onDismissErrorMessage = {
            viewModel.onAction(
                action = ChangePasswordViewModel.ChangePasswordScreenAction.DismissErrorMessage,
            )
        },
    )
}
