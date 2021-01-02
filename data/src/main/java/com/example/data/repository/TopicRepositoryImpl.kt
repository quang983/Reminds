package com.example.data.repository

import com.example.data.local.model.TopicDataGroupMapper
import com.example.data.local.model.TopicDataModel
import com.example.data.local.source.TopicGroupSource
import com.example.domain.model.TopicGroupEntity
import com.example.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val topicDataGroupMapper: TopicDataGroupMapper,
    private val source: TopicGroupSource
) : TopicRepository {
    override suspend fun fetchAllTopicGroups(): Flow<List<TopicGroupEntity>> = source
        .fetchAll().map { it -> it.map { topicDataGroupMapper.toEntity(it) } }.conflate()

    override suspend fun insertDatas(datas: List<TopicGroupEntity>) {
        source.inserts(datas.map {
            TopicDataModel(it.id, it.name)
        })
    }

    override suspend fun updateDatas(datas: List<TopicGroupEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDatas(datas: List<TopicGroupEntity>) {
        TODO("Not yet implemented")
    }
}