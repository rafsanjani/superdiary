package com.foreverrafs.superdiary.domain

import com.foreverrafs.superdiary.domain.model.Diary

interface Synchronizer {
    sealed class SyncOperation {
        data class Save(val diary: Diary) : SyncOperation()
        data class Delete(val diary: Diary) : SyncOperation()
    }

    suspend fun startListening()
    suspend fun sync(operation: SyncOperation): Boolean
    fun stopListening()
}

// A no-op Synchronizer for tests and environments where syncing is not needed
object NoOpSynchronizer : Synchronizer {
    override suspend fun startListening() { /* no-op */ }
    override suspend fun sync(operation: Synchronizer.SyncOperation): Boolean = true
    override fun stopListening() { /* no-op */ }
}
