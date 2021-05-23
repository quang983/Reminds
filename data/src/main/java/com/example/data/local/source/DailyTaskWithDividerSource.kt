package com.example.data.local.source

import com.example.common.base.model.daily.DailyTaskEntity
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import kotlinx.coroutines.flow.Flow

interface DailyTaskWithDividerSource : BaseSource<DailyTaskEntity> {
    suspend fun getAllDataFlow()  : Flow<List<DailyTaskWithDividerEntity>>
}