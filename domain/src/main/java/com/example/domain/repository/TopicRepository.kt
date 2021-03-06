package com.example.domain.repository

import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface TopicRepository : BaseRepository<TopicGroupEntity> {
    suspend fun fetchAllTopicFlowGroups(typeGroup: Int): Flow<List<TopicGroupEntity>>

    suspend fun fetchAllTopicGroups(): List<TopicGroupEntity>

    suspend fun getFastTopicUseCase(): Flow<TopicGroupEntity>

    suspend fun getTopicByIdUseCase(id: Long): Flow<TopicGroupEntity?>

    suspend fun findTopicByIdUseCase(id: Long): TopicGroupEntity?
}