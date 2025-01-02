package com.foreverrafs.superdiary.data

import okio.Path

interface DataStorePathResolver {
    fun resolve(filename: String): Path
}
