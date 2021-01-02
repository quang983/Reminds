package com.example.data.local.source

import com.example.data.local.model.TopicDataModel
import kotlinx.coroutines.flow.Flow

interface TopicGroupSource : BaseSource<TopicDataModel> {
    suspend fun fetchAll(): Flow<List<TopicDataModel>>
}