package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow

class GetFavoriteDiariesUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(): Flow<List<Diary>> = dataSource.fetchFavorites()
}
