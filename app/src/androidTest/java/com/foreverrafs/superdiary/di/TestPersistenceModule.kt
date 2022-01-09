package com.foreverrafs.superdiary.di

import android.app.Application
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.foreverrafs.domain.business.repository.DataSource
import com.foreverrafs.domain.business.repository.RepositoryImpl
import com.foreverrafs.superdiary.data.MockDataSource
import com.foreverrafs.superdiary.framework.datasource.local.database.DiaryDao
import com.foreverrafs.superdiary.framework.datasource.local.database.DiaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestPersistenceModule {
    @Singleton
    @Provides
    fun provideDiaryDatabase(appContext: Application): DiaryDatabase {
        return Room
            .inMemoryDatabaseBuilder(appContext, DiaryDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDataStore(appContext: Application): DataStore<Preferences> {
        return appContext.dataStore
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(appContext: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    @Singleton
    @Provides
    fun provideRoomDataSource(
    ): com.foreverrafs.domain.business.repository.DataSource {
        return MockDataSource()
    }


    @Singleton
    @Provides
    fun provideDiaryRepository(dataSource: com.foreverrafs.domain.business.repository.DataSource): com.foreverrafs.domain.business.repository.RepositoryImpl {
        return com.foreverrafs.domain.business.repository.RepositoryImpl(dataSource = dataSource)
    }

    @Singleton
    @Provides
    fun provideDiaryDao(database: DiaryDatabase): DiaryDao {
        return database.diaryDao()
    }
}