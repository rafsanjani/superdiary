package com.foreverrafs.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import com.foreverrafs.superdiary.core.authentication.R
import kotlinx.coroutines.suspendCancellableCoroutine

class AndroidBiometricAuth(
    context: Context,
) : BiometricAuth {
    private val biometricManager = BiometricManager.from(context.applicationContext)

    override fun canAuthenticate(): Boolean {
        val result = biometricManager.canAuthenticate(BIOMETRIC_STRONG)

        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    override suspend fun startBiometricAuth(uiContext: AuthUiContext): BiometricAuth.AuthResult =
        suspendCancellableCoroutine { continuation ->
            val activity = (uiContext as? AndroidAuthUiContext)?.activity

            if (activity == null) {
                continuation.resumeWith(
                    Result.failure(
                        IllegalStateException("Biometric authentication requires an AndroidAuthUiContext"),
                    ),
                )
                return@suspendCancellableCoroutine
            }

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.biometric_login_title))
                .setSubtitle(activity.getString(R.string.biometric_login_subtitle))
                .setNegativeButtonText(activity.getString(R.string.biometric_login_cancel))
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .build()

            val biometricPrompt = BiometricPrompt(
                activity,
                object : BiometricPrompt.AuthenticationCallback() {
                    var isResumed = false

                    private fun resumeOnce(result: Result<BiometricAuth.AuthResult>) {
                        if (!isResumed) {
                            isResumed = true
                            continuation.resumeWith(result)
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        resumeOnce(
                            Result.success(
                                BiometricAuth.AuthResult.Success,
                            ),
                        )
                    }

                    override fun onAuthenticationFailed() {
                        // If the authentication failed because of wrong fingerprint or a wrong passcode.
                        // Do nothing!
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        // authentication fails because user dismisses the dialog
                        resumeOnce(
                            Result.success(
                                BiometricAuth.AuthResult.Failed,
                            ),
                        )
                    }
                },
            )

            biometricPrompt.authenticate(promptInfo)
        }
}
