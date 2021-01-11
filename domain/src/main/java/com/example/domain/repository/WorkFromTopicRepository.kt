package com.example.domain.repository

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface WorkFromTopicRepository : BaseRepository<WorkDataEntity> {
    //no checked
    suspend fun fetchAllWorkFromTopic(idGroup: Long): Flow<List<WorkDataEntity>>
    //include checked
    suspend fun fetchAllWorksFromTopic(idGroup: Long): Flow<List<WorkDataEntity>>
}