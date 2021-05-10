package com.example.data.repository

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.data.local.source.DailyTaskWithDividerSource
import com.example.domain.repository.DailyTaskRepository
import kotlinx.coroutines.flow.Flow

class DailyTaskRepositoryImpl(private val dailyTaskWithDividerSource: DailyTaskWithDividerSource)  : BaseRepositoryImpl<DailyTaskWithDividerEntity>(dailyTaskWithDividerSource),DailyTaskRepository {
    override suspend fun getAllDataFlow(): Flow<List<DailyTaskWithDividerEntity>> {
        return dailyTaskWithDividerSource.getAllDataFlow()
    }
}