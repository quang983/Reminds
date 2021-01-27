package com.example.data.local.source

import com.example.common.base.model.TopicGroupEntity
import kotlinx.coroutines.flow.Flow

interface TopicGroupSource : BaseSource<TopicGroupEntity> {
    suspend fun fetchAllFlow(): Flow<List<TopicGroupEntity>>

    suspend fun fetchAll(): List<TopicGroupEntity>

    suspend fun getTodayTopic(startTime: Long, endTime: Long): TopicGroupEntity?
}