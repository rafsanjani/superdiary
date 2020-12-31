package com.foreverrafs.superdiary.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.room.Room
import com.foreverrafs.superdiary.business.repository.DataSource
import com.foreverrafs.superdiary.business.repository.DiaryRepository
import com.foreverrafs.superdiary.business.usecase.add.AddDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.common.DeleteDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.diarylist.DiaryListInteractor
import com.foreverrafs.superdiary.business.usecase.diarylist.GetAllDiariesUseCase
import com.foreverrafs.superdiary.business.usecase.diarylist.SearchDiaryUseCase
import com.foreverrafs.superdiary.framework.datasource.local.RoomDataSource
import com.foreverrafs.superdiary.framework.datasource.local.database.DiaryDao
import com.foreverrafs.superdiary.framework.datasource.local.database.DiaryDatabase
import com.foreverrafs.superdiary.framework.datasource.local.mapper.DiaryMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDiaryDatabase(app: Application): DiaryDatabase {
        return Room
            .databaseBuilder(app, DiaryDatabase::class.java, DiaryDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDataStore(app: Application): DataStore<Preferences> {
        return app.createDataStore(name = "settings")
    }


    @Singleton
    @Provides
    fun provideDiaryDao(database: DiaryDatabase): DiaryDao {
        return database.diaryDao()
    }

    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @Provides
    fun provideDataMapper(): DiaryMapper = DiaryMapper()

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
    fun provideDiaryRepository(dataSource: DataSource): DiaryRepository {
        return DiaryRepository(dataSource = dataSource)
    }


    @Provides
    @Singleton
    fun provideDiaryListInteractors(repo: DiaryRepository): DiaryListInteractor {
        return DiaryListInteractor(
            SearchDiaryUseCase(repo),
            GetAllDiariesUseCase(repo),
            DeleteDiaryUseCase(repo)
        )
    }

    @Provides
    @Singleton
    fun provideAddDiaryUseCase(repo: DiaryRepository): AddDiaryUseCase {
        return AddDiaryUseCase(repo)
    }
}