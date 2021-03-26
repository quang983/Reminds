package com.example.framework.source

import androidx.room.Transaction
import com.example.common.base.model.WorkDataEntity
import com.example.data.local.source.WorkFromTopicSource
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import com.example.framework.local.database.model.WorkFoTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkFromTopicSourceImpl @Inject constructor(
    private val dao: LocalWorkFromTopicDao
) : WorkFromTopicSource {
    override suspend fun fetchAllFlow(idGroup: Long): Flow<List<WorkDataEntity>> {
        return dao.fetchWorkFromTopicDataFlow(idGroup).distinctUntilChanged().map { it ->
            it.listWork.map {
                it.convert()
            }
        }.conflate()
    }

    override suspend fun fetchAll(idGroup: Long): List<WorkDataEntity> {
        return dao.fetchWorkFromTopicData(idGroup).let { it ->
            it.listWork.map {
                it.convert()
            }
        }
    }

    override suspend fun getWorkById(idWork: Long): WorkDataEntity? {
        return dao.findById(idWork)?.let { it ->
            it.convert()
        }
    }

    override suspend fun insert(data: WorkDataEntity): Long {
        return dao.insert(
            WorkFoTopic().copy(data)
        )
    }

    override suspend fun inserts(datas: List<WorkDataEntity>) {
        dao.inserts(*datas.map {
            WorkFoTopic().copy(it)
        }.toTypedArray())
    }

    @Transaction
    override suspend fun update(data: WorkDataEntity) {
        dao.updateData(
            WorkFoTopic().copy(data)
        )
    }

    override suspend fun updates(datas: List<WorkDataEntity>) {
        dao.updateDatas(*datas.map {
            WorkFoTopic().copy(it)
        }.toTypedArray())
    }

    override suspend fun delete(datas: WorkDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<WorkDataEntity>) {
        dao.deleteDatas(*datas.map {
            WorkFoTopic().copy(it)
        }.toTypedArray())
    }
}
