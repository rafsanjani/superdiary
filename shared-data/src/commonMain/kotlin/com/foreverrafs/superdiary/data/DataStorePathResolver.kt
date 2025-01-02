package com.foreverrafs.superdiary.data

import okio.Path

fun interface DataStorePathResolver {
    fun resolve(filename: String): Path
}
