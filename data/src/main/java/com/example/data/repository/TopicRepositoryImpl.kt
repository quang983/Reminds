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
    override suspend fun fetchAllTopicFlowGroups(): Flow<List<TopicGroupEntity>> = source
        .fetchAllFlow().conflate()

    override suspend fun fetchAllTopicGroups(): List<TopicGroupEntity> = source.fetchAll()

    override suspend fun getFastTopicUseCase(): TopicGroupEntity {
        return source.getFastTopic()
    }

    override suspend fun insertData(data: TopicGroupEntity): Long {
        return source.insert(TopicGroupEntity(data.id, data.name))
    }

    override suspend fun insertDatas(datas: List<TopicGroupEntity>) {
    }

    override suspend fun updateData(data: TopicGroupEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDatas(datas: List<TopicGroupEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDatas(datas: List<TopicGroupEntity>) {
        return source.deletes(datas = datas)
    }
}