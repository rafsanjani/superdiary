package com.foreverrafs.superdiary.diary.inject

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
@AppScope
interface DataComponent : DatabaseComponent {
    @Provides
    @AppScope
    fun provideAddDiaryUseCase(dataSource: DataSource): AddDiaryUseCase =
        AddDiaryUseCase(dataSource)

    @Provides
    @AppScope
    fun provideDeleteDiaryUseCase(dataSource: DataSource): DeleteDiaryUseCase =
        DeleteDiaryUseCase(dataSource)

    @Provides
    @AppScope
    fun provideGetAllDiariesUseCase(dataSource: DataSource): GetAllDiariesUseCase =
        GetAllDiariesUseCase(dataSource)

    @Provides
    @AppScope
    fun searchDiaryUseCase(dataSource: DataSource): SearchDiaryUseCase =
        SearchDiaryUseCase(dataSource)

    @Provides
    @AppScope
    fun provideDataBase(databaseDriver: DatabaseDriver): Database = Database(databaseDriver)

    @Provides
    @AppScope
    fun provideDataSource(database: Database): DataSource = LocalDataSource(database)
}