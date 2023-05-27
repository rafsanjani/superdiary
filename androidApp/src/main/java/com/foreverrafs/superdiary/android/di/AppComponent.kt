package com.foreverrafs.superdiary.android.di

import android.content.Context
import com.foreverrafs.superdiary.AndroidDatabaseDriver
import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.diary.DatabaseDriver
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryUseCase
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class AppComponent {
    @Provides
    fun provideAddDiaryUseCase(dataSource: DataSource): AddDiaryUseCase =
        AddDiaryUseCase(dataSource)

    @Provides
    fun provideDeleteDiaryUseCase(dataSource: DataSource): DeleteDiaryUseCase =
        DeleteDiaryUseCase(dataSource)

    @Provides
    fun provideGetAllDiariesUseCase(dataSource: DataSource): GetAllDiariesUseCase =
        GetAllDiariesUseCase(dataSource)

    @Provides
    fun searchDiaryUseCase(dataSource: DataSource): SearchDiaryUseCase =
        SearchDiaryUseCase(dataSource)

    @Provides
    fun provideDatabaseDriver(context: Context): DatabaseDriver =
        AndroidDatabaseDriver(context)

    @Provides
    fun provideDataBase(databaseDriver: DatabaseDriver): Database = Database(databaseDriver)

    @Provides
    fun provideDataSource(database: Database): DataSource = LocalDataSource(database)
}