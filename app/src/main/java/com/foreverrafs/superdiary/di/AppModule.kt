package com.foreverrafs.superdiary.di

import android.app.Application
import android.content.SharedPreferences
import com.foreverrafs.superdiary.business.repository.DiaryRepository
import com.foreverrafs.superdiary.business.usecase.add.AddDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.common.DeleteDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.diarylist.DiaryListInteractor
import com.foreverrafs.superdiary.business.usecase.diarylist.GetAllDiariesUseCase
import com.foreverrafs.superdiary.business.usecase.diarylist.SearchDiaryUseCase
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

    @Provides
    @Singleton
    fun provideNotificationScheduler(
        appContext: Application,
        prefs: SharedPreferences
    ): NotificationScheduler {
        return NotificationScheduler(appContext, prefs)
    }
}