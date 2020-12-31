package com.example.domain.repository

import com.example.domain.base.BaseRepository
import com.example.domain.model.TopicGroupEntity

interface TopicRepository : BaseRepository<TopicGroupEntity> {
    suspend fun fetchAllTopicGroups(): List<TopicGroupEntity>
}