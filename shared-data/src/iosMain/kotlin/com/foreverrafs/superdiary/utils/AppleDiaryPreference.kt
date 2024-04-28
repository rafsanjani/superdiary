package com.foreverrafs.superdiary.utils

import com.foreverrafs.superdiary.data.utils.DiaryPreference
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class AppleDiaryPreference : DiaryPreference() {
    @OptIn(ExperimentalForeignApi::class)
    override fun getDataStorePath(filename: String): String {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val path = requireNotNull(documentDirectory).path + "/$filename"

        return path
    }
}
