package com.foreverrafs.superdiary.business.data

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.Repository
import com.foreverrafs.superdiary.business.usecase.add.AddDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.common.DeleteDiaryUseCase
import com.foreverrafs.superdiary.business.usecase.diarylist.GetAllDiariesUseCase
import com.foreverrafs.superdiary.business.usecase.diarylist.SearchDiaryUseCase
import com.foreverrafs.superdiary.framework.datasource.local.dto.DiaryDto
import com.foreverrafs.superdiary.framework.datasource.local.mapper.DiaryMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin.random.Random


const val FILE_NAME = "diary_list.json"

object DependenciesInjector {
    private val entityMapper = DiaryMapper()

    private fun provideDiariesFromFile(): List<Diary> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val diaryListType = Types.newParameterizedType(
            List::class.java,
            DiaryDto::class.java
        )

        val adapter = moshi.adapter<List<DiaryDto>>(diaryListType)

        val diaryJson = javaClass.classLoader!!.getResource(FILE_NAME).readText()

        val list = adapter.fromJson(diaryJson)


        val domainList = entityMapper.mapToDomain(list!!).toMutableList().also {
            //let's add one entry for today
            it.add(
                Diary(id = Random.nextLong(), message = "Hello World")
            )
        }

        return domainList
    }

    fun provideTestDataSource(): Repository {
        return TestDiaryRepository(
            provideDiariesFromFile().toMutableList()
        )
    }

    fun provideAddDiaryUseCase(): AddDiaryUseCase {
        return AddDiaryUseCase(provideTestDataSource())
    }

    fun provideDeleteDiaryUseCase(): DeleteDiaryUseCase {
        return DeleteDiaryUseCase(provideTestDataSource())
    }

    fun provideGetAllDiaryUseCase(): GetAllDiariesUseCase{
        return GetAllDiariesUseCase(provideTestDataSource())
    }

    fun provideSearchDiaryUseCase(): SearchDiaryUseCase {
        return SearchDiaryUseCase(provideTestDataSource())
    }
}