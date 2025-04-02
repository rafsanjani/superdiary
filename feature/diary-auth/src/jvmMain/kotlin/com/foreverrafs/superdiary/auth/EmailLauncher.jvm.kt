package com.foreverrafs.superdiary.auth

import java.awt.Desktop
import java.io.IOException

actual fun openDefaultEmailApp() {
    try {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
            Desktop.getDesktop().mail()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
