package com.example.domain.repository

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface DailyTaskRepository : BaseRepository<DailyTaskWithDividerEntity> {
    suspend fun getAllDataFlow(): Flow<List<DailyTaskWithDividerEntity>>

    suspend fun insertsData(datas: List<DailyTaskWithDividerEntity>)

    suspend fun getDetailById(id: Long): Flow<DailyTaskWithDividerEntity>
}