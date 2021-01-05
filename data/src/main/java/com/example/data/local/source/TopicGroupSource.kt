package com.example.data.local.source

import com.example.common.base.model.TopicGroupEntity
import kotlinx.coroutines.flow.Flow

interface TopicGroupSource : BaseSource<TopicGroupEntity> {
    suspend fun fetchAll(): Flow<List<TopicGroupEntity>>
}