package com.example.framework.source

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
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkFromTopicSourceImpl @Inject constructor(
    private val dao: LocalWorkFromTopicDao,
    private val daoWorkWithChild: LocalWorkWithChildDao,
    private val localContentFromWorkDao: LocalContentFromWorkDao
) : WorkFromTopicSource {
    override suspend fun fetchAll(idGroup: Long): Flow<List<WorkDataEntity>> {
        return dao.fetchWorkFromTopicData(idGroup).map { it ->
            it.listWork.map { it ->
                WorkDataEntity(
                    it.workGroup.id,
                    it.workGroup.name,
                    it.workGroup.idOwnerGroup,
                    it.listContent.map {
                        ContentDataEntity(
                            it.idContent,
                            it.name,
                            it.idOwnerWork
                        )
                    } as ArrayList<ContentDataEntity>
                )
            }
        }.conflate()
    }

    override suspend fun insert(data: WorkDataEntity): Long {
        return dao.insert(WorkFoTopic(data.id, data.name, data.groupId)).apply {
            localContentFromWorkDao.inserts(*data.listContent.map { ContentFoWork(it.id, it.name, it.idOwnerWork) }.toTypedArray())
        }
    }

    override suspend fun inserts(datas: WorkDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun update(datas: WorkDataEntity) {
        TODO("Not yet implemented")
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
