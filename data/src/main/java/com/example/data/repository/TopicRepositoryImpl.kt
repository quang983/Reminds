package com.example.data.repository

import com.example.common.base.model.TopicGroupEntity
import com.example.data.local.source.TopicGroupSource
import com.example.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val source: TopicGroupSource
) : TopicRepository {
    override suspend fun fetchAllTopicFlowGroups(typeGroup : Int): Flow<List<TopicGroupEntity>> = source
        .fetchAllFlow(typeGroup).conflate()

    override suspend fun fetchAllTopicGroups(): List<TopicGroupEntity> = source.fetchAll()

    override suspend fun getFastTopicUseCase(): Flow<TopicGroupEntity> {
        return source.getFastTopic().conflate()
    }

    override suspend fun getTopicByIdUseCase(id: Long): Flow<TopicGroupEntity?> {
        return source.getTopic(id).conflate()
    }

    override suspend fun findTopicByIdUseCase(id: Long): TopicGroupEntity? {
        return source.findTopic(id)
    }

    override suspend fun insertData(data: TopicGroupEntity): Long {
        return source.insert(data)
    }

    override suspend fun insertDatas(datas: List<TopicGroupEntity>) {
    }

    override suspend fun updateData(data: TopicGroupEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDatas(datas: List<TopicGroupEntity>) {
        source.updates(datas)
    }

    override suspend fun deleteDatas(datas: List<TopicGroupEntity>) {
        return source.deletes(datas = datas)
    }
}