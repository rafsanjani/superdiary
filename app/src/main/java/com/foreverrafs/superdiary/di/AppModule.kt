package com.foreverrafs.superdiary.di

import android.app.Application
import android.content.SharedPreferences
import com.foreverrafs.superdiary.business.repository.DataSource
import com.foreverrafs.superdiary.business.repository.Repository
import com.foreverrafs.superdiary.business.repository.RepositoryImpl
import com.foreverrafs.superdiary.business.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.business.usecase.SearchDiaryUseCase
import com.foreverrafs.superdiary.framework.datasource.local.mapper.DiaryMapper
import com.foreverrafs.superdiary.framework.scheduler.NotificationScheduler
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
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @Provides
    fun provideDataMapper(): DiaryMapper = DiaryMapper()

    @Provides
    @Singleton
    fun provideSearchDiaryUseCase(repository: Repository): SearchDiaryUseCase {
        return SearchDiaryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteDiaryUseCase(repository: Repository): DeleteDiaryUseCase {
        return DeleteDiaryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllDiaryUseCase(repository: Repository): GetAllDiariesUseCase {
        return GetAllDiariesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideRepository(dataSource: DataSource): Repository = RepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideAddDiaryUseCase(repository: Repository): AddDiaryUseCase {
        return AddDiaryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideNotificationScheduler(
        appContext: Application,
        prefs: SharedPreferences
    ): NotificationScheduler {
        return NotificationScheduler(appContext, prefs)
    }
}