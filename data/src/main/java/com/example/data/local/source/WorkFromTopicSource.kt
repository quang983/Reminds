package com.example.data.local.source

import com.example.common.base.model.WorkDataEntity
import kotlinx.coroutines.flow.Flow

interface WorkFromTopicSource :BaseSource<WorkDataEntity>{
    suspend fun fetchAll(idGroup: Long): Flow<List<WorkDataEntity>>
}