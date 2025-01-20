package com.foreverrafs.auth

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import kotlinx.coroutines.suspendCancellableCoroutine

class AndroidBiometricAuth(
    private val contextProvider: AndroidContextProvider,
) : BiometricAuth {
    private val biometricManager =
        BiometricManager.from(contextProvider.getContext() ?: error("Context is null"))

    override fun canAuthenticate(): Boolean {
        val result = biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)

        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    override suspend fun startBiometricAuth(): BiometricAuth.AuthResult =
        suspendCancellableCoroutine { continuation ->
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for superdiary")
                .setSubtitle("Log in using your biometric credential")
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .build()

            val biometricPrompt = BiometricPrompt(
                contextProvider.getContext() as? AppCompatActivity
                    ?: error("Context is not an activity!s"),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        continuation.resumeWith(
                            Result.success(
                                BiometricAuth.AuthResult.Success,
                            ),
                        )
                    }

                    override fun onAuthenticationFailed() {
                        continuation.resumeWith(
                            Result.success(
                                BiometricAuth.AuthResult.Failed,
                            ),
                        )
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        continuation.resumeWith(
                            Result.success(
                                BiometricAuth.AuthResult.Error(
                                    Exception(errString.toString()),
                                ),
                            ),
                        )
                    }
                },
            )

            biometricPrompt.authenticate(promptInfo)
        }
}
