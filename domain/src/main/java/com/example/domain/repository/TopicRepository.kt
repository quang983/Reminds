package com.example.domain.repository

import com.example.domain.base.BaseRepository
import com.example.common.base.model.TopicGroupEntity
import kotlinx.coroutines.flow.Flow

interface TopicRepository : BaseRepository<TopicGroupEntity> {
    suspend fun fetchAllTopicGroups(): Flow<List<TopicGroupEntity>>
}