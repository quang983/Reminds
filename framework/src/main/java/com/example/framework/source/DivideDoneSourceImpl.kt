package com.example.framework.source

import com.example.common.base.model.daily.DailyDivideTaskDoneEntity
import com.example.data.local.source.DivideDoneSource
import com.example.framework.local.database.dao.LocalDailyDividerDoneDao
import com.example.framework.local.database.model.DailyDivideTaskDone
import javax.inject.Inject

class DivideDoneSourceImpl @Inject constructor(private val dao: LocalDailyDividerDoneDao) : DivideDoneSource {
    override suspend fun insert(data: DailyDivideTaskDoneEntity): Long {
        return 0
    }

    override suspend fun inserts(datas: List<DailyDivideTaskDoneEntity>) {
        dao.inserts(*datas.map {
            DailyDivideTaskDone(it.id, it.idGroup, it.name, it.doneTime)
        }.toTypedArray())
    }

    override suspend fun update(data: DailyDivideTaskDoneEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updates(datas: List<DailyDivideTaskDoneEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(datas: DailyDivideTaskDoneEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<DailyDivideTaskDoneEntity>) {
        TODO("Not yet implemented")
    }
}