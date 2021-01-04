package com.example.data.local.source

import com.example.common.base.model.WorkDataEntity
import kotlinx.coroutines.flow.Flow

interface WorkFromTopicSource {
    suspend fun fetchAll(idGroup: Long): Flow<List<WorkDataEntity>>
}