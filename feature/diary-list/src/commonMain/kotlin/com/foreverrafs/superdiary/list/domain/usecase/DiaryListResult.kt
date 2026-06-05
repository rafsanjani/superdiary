package com.foreverrafs.superdiary.list.domain.usecase

import androidx.paging.PagingData
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary

typealias DiaryListResult = Result<PagingData<Diary>>
