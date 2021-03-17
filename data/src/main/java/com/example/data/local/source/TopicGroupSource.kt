package com.example.data.local.source

import com.example.common.base.model.TopicGroupEntity
import kotlinx.coroutines.flow.Flow

interface TopicGroupSource : BaseSource<TopicGroupEntity> {
    suspend fun fetchAllFlow(typeGroup : Int): Flow<List<TopicGroupEntity>>

    suspend fun fetchAll(): List<TopicGroupEntity>

    suspend fun getFastTopic(): Flow<TopicGroupEntity>

    suspend fun getTopic(id: Long): Flow<TopicGroupEntity?>

    suspend fun findTopic(id: Long): TopicGroupEntity?
}