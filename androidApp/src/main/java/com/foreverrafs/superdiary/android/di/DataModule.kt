//package com.foreverrafs.superdiary.android.di
//
//import android.content.Context
//import com.foreverrafs.superdiary.AndroidDatabaseDriver
//import com.foreverrafs.superdiary.diary.Database
//import com.foreverrafs.superdiary.diary.DatabaseDriver
//import com.foreverrafs.superdiary.diary.datasource.DataSource
//import com.foreverrafs.superdiary.diary.datasource.LocalDataSource
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object DataModule {
//    @Provides
//    @Singleton
//    fun provideDatabaseDriver(@ApplicationContext context: Context): DatabaseDriver =
//        AndroidDatabaseDriver(context)
//
//    @Provides
//    @Singleton
//    fun provideDataBase(databaseDriver: DatabaseDriver): Database = Database(databaseDriver)
//
//    @Provides
//    @Singleton
//    fun provideDataSource(database: Database): DataSource = LocalDataSource(database)
//}