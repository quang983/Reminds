package com.example.data.repository

import com.example.data.local.model.TopicDataGroupMapper
import com.example.data.local.source.TopicGroupSource
import com.example.domain.model.TopicGroupEntity
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class TopicRepositoryImpl @Inject constructor(
    private val topicDataGroupMapper: TopicDataGroupMapper,
    private val source: TopicGroupSource
) : TopicRepository {
    override suspend fun fetchAllTopicGroups(): List<TopicGroupEntity> {
        return source.fetchAll().map {
            topicDataGroupMapper.toEntity(it)
        }
    }
}