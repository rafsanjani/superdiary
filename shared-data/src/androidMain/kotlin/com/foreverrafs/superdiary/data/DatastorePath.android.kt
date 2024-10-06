package com.foreverrafs.superdiary.data

import java.io.File

// This isn't a very good idea but oh well, here we go
var androidFilesDirectory: File = File("")

actual fun getDatastorePath(filename: String): String = androidFilesDirectory.resolve(filename).absolutePath
