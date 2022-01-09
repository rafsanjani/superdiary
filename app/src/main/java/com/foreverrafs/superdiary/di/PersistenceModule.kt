package com.foreverrafs.superdiary.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.foreverrafs.domain.feature_diary.repository.DataSource
import com.foreverrafs.domain.feature_diary.repository.RepositoryImpl
import com.foreverrafs.superdiary.data.local.RoomDataSource
import com.foreverrafs.superdiary.data.local.database.DiaryDao
import com.foreverrafs.superdiary.data.local.database.DiaryDatabase
import com.foreverrafs.superdiary.data.local.mapper.DiaryMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


// At the top level of your kotlin file
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {
    @Singleton
    @Provides
    fun provideDiaryDatabase(appContext: Application): DiaryDatabase {
        return Room
            .databaseBuilder(appContext, DiaryDatabase::class.java, DiaryDatabase.DATABASE_NAME)
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
    fun provideRoomDataSource(
        diaryDao: DiaryDao,
        diaryMapper: DiaryMapper
    ): DataSource {
        return RoomDataSource(diaryDao, diaryMapper)
    }


    @Singleton
    @Provides
    fun provideDiaryRepository(dataSource: DataSource): RepositoryImpl {
        return RepositoryImpl(dataSource = dataSource)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(appContext: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    @Singleton
    @Provides
    fun provideDiaryDao(database: DiaryDatabase): DiaryDao {
        return database.diaryDao()
    }
}