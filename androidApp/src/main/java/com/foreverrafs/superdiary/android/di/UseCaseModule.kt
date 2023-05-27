//package com.foreverrafs.superdiary.android.di
//
//import com.foreverrafs.superdiary.diary.datasource.DataSource
//import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
//import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
//import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
//import com.foreverrafs.superdiary.diary.usecase.SearchDiaryUseCase
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//
//@Module
//@InstallIn(SingletonComponent::class)
//object UseCaseModule {
//    @Provides
//    fun provideAddDiaryUseCase(dataSource: DataSource): AddDiaryUseCase =
//        AddDiaryUseCase(dataSource)
//
//    @Provides
//    fun provideDeleteDiaryUseCase(dataSource: DataSource): DeleteDiaryUseCase =
//        DeleteDiaryUseCase(dataSource)
//
//    @Provides
//    fun provideGetAllDiariesUseCase(dataSource: DataSource): GetAllDiariesUseCase =
//        GetAllDiariesUseCase(dataSource)
//
//    @Provides
//    fun searchDiaryUseCase(dataSource: DataSource): SearchDiaryUseCase =
//        SearchDiaryUseCase(dataSource)
//}