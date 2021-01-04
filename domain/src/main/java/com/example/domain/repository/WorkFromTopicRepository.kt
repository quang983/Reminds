package com.example.domain.repository

import com.example.common.base.model.WorkDataEntity
import kotlinx.coroutines.flow.Flow

interface WorkFromTopicRepository {
    suspend fun fetchAllWorkFromTopic(idGroup : Long): Flow<List<WorkDataEntity>>
}