package com.foreverrafs.superdiary

import android.content.Context
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import db.KmpSuperDiaryDB
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.locks.ReentrantLock

// Will be initialized during Application's onCreate
var appContext: Context? = null

class FlowLocalDataSource : DataSource by LocalDataSource.getInstance() {
    private val queries = LocalDatabaseFactory.getSuperDiaryDB().databaseQueries

    override fun fetchAllAsFlow(): Flow<List<Diary>> {
        return queries.selectAll(mapper = { id, entry, date -> Diary(id, entry, date) })
            .asFlow()
            .mapToList()
    }
}

actual class LocalDatabaseFactory {
    actual companion object {
        private var superDiaryDB: KmpSuperDiaryDB? = null
        private var lock = ReentrantLock()

        actual fun getSuperDiaryDB(): KmpSuperDiaryDB {
            if (superDiaryDB != null) return superDiaryDB!!

            synchronized(lock) {
                val driver =
                    AndroidSqliteDriver(
                        schema = KmpSuperDiaryDB.Schema,
                        context = appContext ?: throw IllegalArgumentException("appContext is null!"),
                        name = "diary.db",
                    )

                superDiaryDB = KmpSuperDiaryDB(driver)

                return superDiaryDB!!
            }
        }
    }
}