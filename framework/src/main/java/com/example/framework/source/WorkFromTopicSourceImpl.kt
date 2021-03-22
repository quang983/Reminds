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
                            timer = it.timer,
                            isCheckDone = it.isCheckDone
                        )
                    } as ArrayList<ContentDataEntity>,
                    it.doneAll,
                    it.isShowContents,
                    it.hashTag,
                    it.timerReminder
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
                            timer = it.timer,
                            isCheckDone = it.isCheckDone
                        )
                    } as ArrayList<ContentDataEntity>,
                    doneAll = it.doneAll,
                    it.isShowContents,
                    it.hashTag,
                    it.timerReminder
                )
            }
        }
    }

    override suspend fun getWorkById(idWork: Long): WorkDataEntity? {
        return dao.findById(idWork)?.let { it ->
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
                        timer = it.timer,
                        isCheckDone = it.isCheckDone
                    )
                } as ArrayList<ContentDataEntity>,
                doneAll = it.doneAll,
                it.isShowContents,
                it.hashTag,
                it.timerReminder
            )
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
                }.toMutableList(), data.doneAll, isShowContents = data.isShowContents, data.hashTag, data.timerReminder
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
                }.toMutableList(), false, isShowContents = false, false, -1
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
                }.toMutableList(), data.doneAll, data.isShowContents, data.hashTag, data.timerReminder
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
                }.toMutableList(), it.doneAll, it.isShowContents, it.hashTag, it.timerReminder
            )
        }.toTypedArray())
    }

    override suspend fun delete(datas: WorkDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<WorkDataEntity>) {
        dao.deleteDatas(*datas.map { it ->
            WorkFoTopic(
                it.id, it.name, it.groupId,
                it.listContent.map {
                    ContentFoWork(
                        it.id, it.name, it.idOwnerWork, hashTag = it.hashTag,
                        timer = it.timer, isCheckDone = it.isCheckDone
                    )
                }.toMutableList(), it.doneAll, it.isShowContents, it.hashTag, it.timerReminder
            )
        }.toTypedArray())
    }
}
