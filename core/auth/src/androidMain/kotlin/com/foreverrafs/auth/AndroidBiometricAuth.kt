package com.foreverrafs.auth

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import kotlinx.coroutines.suspendCancellableCoroutine

class AndroidBiometricAuth(
    private val contextProvider: AndroidContextProvider,
) : BiometricAuth {
    private val biometricManager =
        BiometricManager.from(contextProvider.getContext() ?: error("Context is null"))

    override fun canAuthenticate(): Boolean {
        val result = biometricManager.canAuthenticate(BIOMETRIC_STRONG)

        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    override suspend fun startBiometricAuth(): BiometricAuth.AuthResult =
        suspendCancellableCoroutine { continuation ->
            val activity = contextProvider.getContext() as? AppCompatActivity
            if (activity == null) {
                continuation.resumeWith(
                    Result.failure(
                        IllegalStateException("Context is not an AppCompatActivity!"),
                    ),
                )
                return@suspendCancellableCoroutine
            }

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for superdiary")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(BIOMETRIC_STRONG)
                .build()

            val biometricPrompt = BiometricPrompt(
                contextProvider.getContext() as? AppCompatActivity
                    ?: error("Context is not an activity!s"),

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
