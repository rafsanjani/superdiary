package com.foreverrafs.auth

interface BiometricAuth {
    sealed interface AuthResult {
        data object Success : AuthResult
        data object Failed : AuthResult
        data class Error(val error: Exception) : AuthResult
    }

    /**
     * Determines whether the device has the necessary hardware for biometrics
     * authentication. On Android, this can be a fingerpring/iris scanner. On
     * iOS this will either be a Face ID or fingerprint scanner
     */
    fun canAuthenticate(): Boolean

    suspend fun startBiometricAuth(): AuthResult
}
