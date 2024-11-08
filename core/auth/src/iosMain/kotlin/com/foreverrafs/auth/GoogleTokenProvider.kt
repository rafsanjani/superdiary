package com.foreverrafs.auth

import kotlin.experimental.ExperimentalObjCName

/**
 * This interface will be implemented on the Swift side using the Sign in
 * with Google package
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "GoogleTokenProvider")
interface GoogleTokenProvider {
    suspend fun getGoogleToken(): String
}
