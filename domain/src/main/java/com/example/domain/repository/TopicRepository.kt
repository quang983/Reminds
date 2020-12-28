package com.example.domain.repository

import com.example.domain.model.TopicGroupEntity

interface TopicRepository {
    suspend fun fetchAllTopicGroups(): List<TopicGroupEntity>
}