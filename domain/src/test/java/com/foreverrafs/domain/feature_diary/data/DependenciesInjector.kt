package com.foreverrafs.domain.feature_diary.data


import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.repository.Repository
import com.foreverrafs.domain.feature_diary.usecase.AddDiaryUseCase
import com.foreverrafs.domain.feature_diary.usecase.DeleteDiaryUseCase
import com.foreverrafs.domain.feature_diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.domain.feature_diary.usecase.SearchDiaryUseCase
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDateTime


const val FILE_NAME = "diary_list.json"

class DateAdapter {
    @ToJson
    fun toJson(date: LocalDateTime): String {
        return date.toString()
    }

    @FromJson
    fun fromJson(json: String): LocalDateTime {
        return LocalDateTime.parse(json)
    }
}

object DependenciesInjector {
    private fun provideDiariesFromFile(): List<Diary> {
        val moshi = Moshi.Builder()
            .add(DateAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val diaryListType = Types.newParameterizedType(
            List::class.java,
            Diary::class.java
        )

        val adapter = moshi.adapter<List<Diary>>(diaryListType)

        val diaryJson = javaClass.classLoader!!.getResource(FILE_NAME).readText()

        val list = adapter.fromJson(diaryJson)!!

        val domainList = list.toMutableList().also {
            //let's add one entry for today
            it.add(
                Diary(
                    message = "Hello World",
                    title = ""
                )
            )
        }

        return domainList
    }

    fun `provideTestRepository()`(): Repository {
        return TestDiaryRepository(
            provideDiariesFromFile().toMutableList()
        )
    }

    fun provideAddDiaryUseCase(): AddDiaryUseCase {
        return AddDiaryUseCase(`provideTestRepository()`())
    }

    fun provideDeleteDiaryUseCase(): DeleteDiaryUseCase {
        return DeleteDiaryUseCase(`provideTestRepository()`())
    }

    fun provideGetAllDiaryUseCase(): GetAllDiariesUseCase {
        return GetAllDiariesUseCase(
            `provideTestRepository()`()
        )
    }

    fun provideSearchDiaryUseCase(): SearchDiaryUseCase {
        return SearchDiaryUseCase(`provideTestRepository()`())
    }
}