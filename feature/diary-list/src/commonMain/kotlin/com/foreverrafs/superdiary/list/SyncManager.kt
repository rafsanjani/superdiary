package com.foreverrafs.superdiary.list

interface SyncManager {
    fun syncDeletedDiaries(ids: List<Int>)
}
