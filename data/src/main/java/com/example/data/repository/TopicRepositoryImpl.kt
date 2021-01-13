package com.example.data.repository

import com.example.common.base.model.TopicGroupEntity
import com.example.data.local.model.TopicDataGroupMapper
import com.example.data.local.source.TopicGroupSource
import com.example.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val topicDataGroupMapper: TopicDataGroupMapper,
    private val source: TopicGroupSource
) : TopicRepository {
    override suspend fun fetchAllTopicGroups(): Flow<List<TopicGroupEntity>> = source
        .fetchAll().conflate()

    override suspend fun insertData(data: TopicGroupEntity): Long {
        return source.insert(TopicGroupEntity(data.id, data.name,data.startDate))
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