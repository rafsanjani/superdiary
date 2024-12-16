package com.foreverrafs.superdiary

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.ui.ExperimentalComposeUiApi
import java.security.MessageDigest

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val key = getSigningKeySHA1(packageName, packageManager)
        setContentView(
            TextView(this).apply {
                text = key
            },
        )
    }
}

fun getSigningKeySHA1(packageName: String, packageManager: PackageManager): String? = try {
    val packageInfo: PackageInfo =
        packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)

    val cert =
        packageInfo.signingInfo?.apkContentsSigners[0]?.toByteArray()

    val messageDigest = MessageDigest.getInstance("SHA1")
    if (cert != null) {
        messageDigest.update(cert)
    }
    val sha1Bytes = messageDigest.digest()
    sha1Bytes.joinToString(":") { String.format("%02X", it) }
} catch (e: Exception) {
    e.printStackTrace()
    null
}
