package com.example.framework.source

import androidx.room.Transaction
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.data.local.source.WorkFromTopicSource
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import com.example.framework.local.database.model.ContentFoWork
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
            it.listWork.map { it ->
                WorkDataEntity(
                    it.id,
                    it.name,
                    it.idOwnerGroup,
                    it.listContent.map {
                        ContentDataEntity(
                            it.idContent,
                            it.name,
                            it.idOwnerWork,
                            hashTag = it.hashTag,
                            timer = it.timer, isCheckDone = it.isCheckDone
                        )
                    } as ArrayList<ContentDataEntity>
                )
            }
        }.conflate()
    }

    override suspend fun fetchAll(idGroup: Long): List<WorkDataEntity> {
        return dao.fetchWorkFromTopicData(idGroup).let { it ->
            it.listWork.map { it ->
                WorkDataEntity(
                    it.id,
                    it.name,
                    it.idOwnerGroup,
                    it.listContent.map {
                        ContentDataEntity(
                            it.idContent,
                            it.name,
                            it.idOwnerWork,
                            hashTag = it.hashTag,
                            timer = it.timer, isCheckDone = it.isCheckDone
                        )
                    } as ArrayList<ContentDataEntity>
                )
            }
        }
    }

    override suspend fun insert(data: WorkDataEntity): Long {
        return dao.insert(
            WorkFoTopic(
                data.id, data.name, data.groupId,
                data.listContent.map {
                    ContentFoWork(
                        it.id, it.name, it.idOwnerWork, hashTag = it.hashTag,
                        timer = it.timer, isCheckDone = it.isCheckDone
                    )
                }.toMutableList()
            )
        )
    }

    override suspend fun inserts(datas: List<WorkDataEntity>) {
        dao.inserts(*datas.map { it ->
            WorkFoTopic(
                it.id, it.name, it.groupId,
                it.listContent.map {
                    ContentFoWork(
                        it.id, it.name, it.idOwnerWork, hashTag = it.hashTag,
                        timer = it.timer, isCheckDone = it.isCheckDone
                    )
                }.toMutableList()
            )
        }.toTypedArray())
    }

    @Transaction
    override suspend fun update(data: WorkDataEntity) {
        dao.updateData(
            WorkFoTopic(
                data.id, data.name, data.groupId,
                data.listContent.map {
                    ContentFoWork(
                        it.id, it.name, it.idOwnerWork, hashTag = it.hashTag,
                        timer = it.timer, isCheckDone = it.isCheckDone
                    )
                }.toMutableList()
            )
        )
    }

    override suspend fun updates(datas: List<WorkDataEntity>) {
        dao.updateDatas(*datas.map { it ->
            WorkFoTopic(
                it.id, it.name, it.groupId,
                it.listContent.map {
                    ContentFoWork(
                        it.id, it.name, it.idOwnerWork, hashTag = it.hashTag,
                        timer = it.timer, isCheckDone = it.isCheckDone
                    )
                }.toMutableList()
            )
        }.toTypedArray())
    }

    override suspend fun delete(datas: WorkDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<WorkDataEntity>) {
        TODO("Not yet implemented")
    }
}
