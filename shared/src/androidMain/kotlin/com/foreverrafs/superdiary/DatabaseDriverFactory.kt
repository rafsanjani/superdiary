package com.foreverrafs.superdiary

import android.content.Context
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import db.KmpSuperDiaryDB
import kotlinx.coroutines.flow.Flow

/**
 * A [LocalDataSource] which continuously emits database values as flow as soon as a change is detected.
 * The whole list will be re-emitted whenever an item is added or removed from the database
 */
class FlowLocalDataSource(private val database: Database) : DataSource by LocalDataSource(database) {
    override fun fetchAllAsFlow(): Flow<List<Diary>> {
        return database.getDatabaseQueries().selectAll(mapper = { id, entry, date -> Diary(id, entry, date) })
            .asFlow()
            .mapToList()
    }
}

actual class DatabaseDriverFactory(private val context: Context) {
    internal actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = KmpSuperDiaryDB.Schema,
            context = context,
            name = "diary.db",
        )
    }
}