package com.example.framework.source

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.data.local.source.DailyTaskWithDividerSource
import com.example.framework.local.database.dao.LocalDailyTaskWithDividerDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DailyTaskWithDividerSourceImpl @Inject constructor(private val withDividerDao : LocalDailyTaskWithDividerDao) : DailyTaskWithDividerSource {
    override suspend fun getAllDataFlow(): Flow<List<DailyTaskWithDividerEntity>> {
        return withDividerDao.getAllDataFlow().map { it ->
            it.map {
                it.convert()
            }
        }
    }

    override suspend fun insert(data: DailyTaskWithDividerEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun inserts(datas: List<DailyTaskWithDividerEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun update(data: DailyTaskWithDividerEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updates(datas: List<DailyTaskWithDividerEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(datas: DailyTaskWithDividerEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<DailyTaskWithDividerEntity>) {
        TODO("Not yet implemented")
    }
}