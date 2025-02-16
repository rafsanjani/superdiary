package com.foreverrafs.superdiary.data.datasource.remote

import com.foreverrafs.superdiary.data.model.DiaryDto
import kotlinx.coroutines.flow.Flow

interface DiaryApi {
    /**
     * Fetch all the diary items from a remote datasource, returning a list of
     * all the items that were successfully fetched. The flow returned from
     * this function will publish data changes to subscribers and will also
     * update in real-time if the data on the network changes
     *
     * @return a list of diary items that were fetched
     */
    fun fetchAll(): Flow<List<DiaryDto>>
}
