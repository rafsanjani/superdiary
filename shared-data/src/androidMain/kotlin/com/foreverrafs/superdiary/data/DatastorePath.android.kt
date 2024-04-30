package com.foreverrafs.superdiary.data

import java.io.File

// This isn't a very good idea but oh well, here we go
var AndroidFilesDir: File = File("")

actual fun getDatastorePath(filename: String): String {
    return AndroidFilesDir.resolve(filename).absolutePath
}
