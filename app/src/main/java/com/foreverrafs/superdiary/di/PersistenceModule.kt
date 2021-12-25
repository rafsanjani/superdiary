package com.foreverrafs.superdiary.di

import android.app.Application
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.foreverrafs.superdiary.business.repository.DataSource
import com.foreverrafs.superdiary.business.repository.RepositoryImpl
import com.foreverrafs.superdiary.framework.datasource.local.RoomDataSource
import com.foreverrafs.superdiary.framework.datasource.local.database.DiaryDao
import com.foreverrafs.superdiary.framework.datasource.local.database.DiaryDatabase
import com.foreverrafs.superdiary.framework.datasource.local.mapper.DiaryMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
        return appContext.createDataStore(name = "settings")
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