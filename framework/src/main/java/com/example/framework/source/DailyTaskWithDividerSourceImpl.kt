package com.example.framework.source

import com.example.common.base.model.daily.DailyTaskEntity
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.data.local.source.DailyTaskWithDividerSource
import com.example.framework.local.database.dao.LocalDailyTaskWithDividerDao
import com.example.framework.local.database.model.DailyTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DailyTaskWithDividerSourceImpl @Inject constructor(private val withDividerDao: LocalDailyTaskWithDividerDao) : DailyTaskWithDividerSource {
    override suspend fun getAllDataFlow(): Flow<List<DailyTaskWithDividerEntity>> {
        return withDividerDao.getAllDataFlow().map { it ->
            it.map {
                it.convert()
            }
        }
    }

    override suspend fun getDetailById(id: Long): Flow<DailyTaskWithDividerEntity> {
        return withDividerDao.getDetailById(id).map {
            it.convert()
        }
    }

    override suspend fun insert(data: DailyTaskEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun inserts(datas: List<DailyTaskEntity>) {
        withDividerDao.inserts(*datas.map {
            DailyTask(it.id, it.name, it.desc, it.createTime, it.endTime, it.remainingTime, it.listDayOfWeek)
        }.toTypedArray())
    }

    override suspend fun update(data: DailyTaskEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updates(datas: List<DailyTaskEntity>) {
        withDividerDao.updateDatas(*datas.map {
            DailyTask(it.id, it.name, it.desc, it.createTime, it.endTime, it.remainingTime, it.listDayOfWeek)
        }.toTypedArray())
    }

    override suspend fun delete(datas: DailyTaskEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<DailyTaskEntity>) {
        TODO("Not yet implemented")
    }
}