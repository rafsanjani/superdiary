package com.foreverrafs.auth.di

import com.foreverrafs.auth.BiometricAuth

class DesktopBiometricAuth : BiometricAuth {
    override fun canAuthenticate(): Boolean = false

    override suspend fun startBiometricAuth(uiContext: com.foreverrafs.auth.AuthUiContext): BiometricAuth.AuthResult {
        TODO("Not yet implemented")
    }
}
