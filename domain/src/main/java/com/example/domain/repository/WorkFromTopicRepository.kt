package com.example.domain.repository

import com.example.common.base.model.WorkDataEntity

interface WorkFromTopicRepository {
    suspend fun fetchAllWorkFromTopic(): List<WorkDataEntity>
}