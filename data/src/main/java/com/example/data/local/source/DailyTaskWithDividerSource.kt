package com.example.data.local.source

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import kotlinx.coroutines.flow.Flow

interface DailyTaskWithDividerSource : BaseSource<DailyTaskWithDividerEntity> {
    suspend fun getAllDataFlow()  : Flow<List<DailyTaskWithDividerEntity>>
}