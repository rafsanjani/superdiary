package com.foreverrafs.superdiary.data

import okio.Path
import okio.Path.Companion.toPath

class JVMDataStorePathResolver : DataStorePathResolver {
    override fun resolve(filename: String): Path = filename.toPath()
}
