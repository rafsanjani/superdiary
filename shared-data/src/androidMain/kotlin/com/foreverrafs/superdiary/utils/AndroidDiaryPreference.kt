package com.foreverrafs.superdiary.utils

import android.content.Context
import com.foreverrafs.superdiary.data.utils.DiaryPreference

class AndroidDiaryPreference(private val context: Context) : DiaryPreference() {
    override fun getDataStorePath(filename: String): String =
        context.filesDir.resolve(filename).absolutePath
}
