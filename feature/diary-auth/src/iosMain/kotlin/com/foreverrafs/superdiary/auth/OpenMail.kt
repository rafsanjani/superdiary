package com.foreverrafs.superdiary.auth

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openMail() {
    NSURL.URLWithString("message://")?.let {
        if (UIApplication.sharedApplication.canOpenURL(url = it)) {
            UIApplication.sharedApplication.openURL(
                url = it,
                options = emptyMap<Any?, Any?>(),
                completionHandler = {},
            )
        }
    }
}
