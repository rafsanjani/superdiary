package com.foreverrafs.superdiary.data

import android.content.Context
import okio.Path
import okio.Path.Companion.toOkioPath

class AndroidDataStorePathResolver(
    private val context: Context,
) : DataStorePathResolver {
    override fun resolve(filename: String): Path =
        context.filesDir.resolve(filename).absoluteFile.toOkioPath()
}
