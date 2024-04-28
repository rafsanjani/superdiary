package com.foreverrafs.superdiary.utils

import android.content.Context
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import okio.Path
import okio.Path.Companion.toPath

class AndroidDiaryPreference(private val context: Context) : DiaryPreference() {
    override fun getDataStorePath(filename: String): Path =
        context.filesDir.resolve(filename).absolutePath.toPath()
}
