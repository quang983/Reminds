package com.example.framework.source

import androidx.room.Transaction
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.data.local.source.WorkFromTopicSource
import com.example.framework.local.database.dao.LocalContentFromWorkDao
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import com.example.framework.local.database.dao.LocalWorkWithChildDao
import com.example.framework.local.database.model.ContentFoWork
import com.example.framework.local.database.model.WorkFoTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkFromTopicSourceImpl @Inject constructor(
    private val dao: LocalWorkFromTopicDao,
    private val localContentFromWorkDao: LocalContentFromWorkDao
) : WorkFromTopicSource {
    override suspend fun fetchAll(idGroup: Long): Flow<List<WorkDataEntity>> {
        return dao.fetchWorkFromTopicData(idGroup).distinctUntilChanged().map { it ->
            it.listWork.map { it ->
                WorkDataEntity(
                    it.workGroup.id,
                    it.workGroup.name,
                    it.workGroup.idOwnerGroup,
                    it.listContent.filter { !it.isChecked }.map {
                        ContentDataEntity(
                            it.idContent,
                            it.name,
                            it.idOwnerWork,
                            it.isChecked
                        )
                    } as ArrayList<ContentDataEntity>
                )
            }
        }.conflate()
    }

    override suspend fun insert(data: WorkDataEntity): Long {
        return dao.insert(WorkFoTopic(data.id, data.name, data.groupId)).apply {
            localContentFromWorkDao.inserts(*data.listContent.map { ContentFoWork(it.id, it.name, it.idOwnerWork, it.isChecked) }.toTypedArray())
        }
    }

    override suspend fun inserts(datas: List<WorkDataEntity>) {
        dao.inserts(*datas.map { WorkFoTopic(it.id, it.name, it.groupId) }.toTypedArray()).apply {
            datas.forEach { it ->
                localContentFromWorkDao.inserts(*it.listContent.map { ContentFoWork(it.id, it.name, it.idOwnerWork, it.isChecked) }.toTypedArray())
            }
        }
    }

    @Transaction
    override suspend fun update(data: WorkDataEntity) {
        dao.updateData(WorkFoTopic(data.id, data.name, data.groupId))
    }

    override suspend fun updates(datas: List<WorkDataEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(datas: WorkDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<WorkDataEntity>) {
        TODO("Not yet implemented")
    }
}
