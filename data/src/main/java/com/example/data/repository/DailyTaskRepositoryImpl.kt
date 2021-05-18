package com.example.data.repository

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.data.local.source.DailyTaskWithDividerSource
import com.example.domain.repository.DailyTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DailyTaskRepositoryImpl @Inject constructor(dailyTaskWithDividerSource: DailyTaskWithDividerSource) :
    BaseRepositoryImpl<DailyTaskWithDividerEntity>(dailyTaskWithDividerSource), DailyTaskRepository {
    override suspend fun getAllDataFlow(): Flow<List<DailyTaskWithDividerEntity>> {
        return flow { emptyList<DailyTaskWithDividerEntity>() }
    }
}