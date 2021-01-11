package com.example.framework.source

import androidx.room.Transaction
import com.example.common.base.model.TopicGroupEntity
import com.example.data.local.source.TopicGroupSource
import com.example.framework.local.database.dao.LocalTopicGroupDao
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import com.example.framework.local.database.model.TopicGroup
import com.example.framework.local.database.model.WorkFoTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TopicGroupSourceImpl @Inject constructor(
    private val dao: LocalTopicGroupDao,
    private val daoWork: LocalWorkFromTopicDao,
) : TopicGroupSource {
    override suspend fun fetchAll(): Flow<List<TopicGroupEntity>> {
        return dao.fetchTopicGroupData().map { it ->
            it.map { it.toDomain(it) }
        }
    }

    @Transaction
    override suspend fun insert(data: TopicGroupEntity): Long {
        val topicGroupDbId = dao.insert(TopicGroup().toData(data))
        daoWork.insert(
            WorkFoTopic(
                id = System.currentTimeMillis(),
                name = "Cơ bản",
                idOwnerGroup = topicGroupDbId,
                listContent = mutableListOf(),
                listContentDone = mutableListOf()
            )
        )
        return topicGroupDbId
    }

    override suspend fun inserts(datas: List<TopicGroupEntity>) {
    }

    override suspend fun update(datas: TopicGroupEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updates(datas: List<TopicGroupEntity>) {
        dao.updateDatas(*datas.map { TopicGroup().toData(it) }.toTypedArray())
    }

    override suspend fun delete(datas: TopicGroupEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<TopicGroupEntity>) {
        dao.deleteDatas(*datas.map { TopicGroup().toData(it) }.toTypedArray())
    }


/*    override suspend fun inserts(datas: List<TopicGroupEntity>) {
        dao.inserts(*datas.map { TopicGroup().toData(it) }.toTypedArray())
    }

    override suspend fun updates(datas: TopicGroupEntity) {
        dao.updateDatas(*datas.map { TopicGroup().toData(it) }.toTypedArray())
    }

    override suspend fun deletes(datas: TopicGroupEntity) {
        dao.deleteDatas(*datas.map { TopicGroup().toData(it) }.toTypedArray())
    }*/
}