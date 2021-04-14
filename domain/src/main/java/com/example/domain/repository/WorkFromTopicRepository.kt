package com.example.domain.repository

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseRepository
import kotlinx.coroutines.flow.Flow

interface WorkFromTopicRepository : BaseRepository<WorkDataEntity> {
    suspend fun fetchAllWorkFromTopicFlow(idGroup: Long): Flow<List<WorkDataEntity>>

    suspend fun fetchAllWorkFromTopic(idGroup: Long): List<WorkDataEntity>

    suspend fun getWorkFromTopicById(idWork: Long): WorkDataEntity?

    //state = 0: not check, 1:check, 2 getAll
    suspend fun getAllWorkDependState(state: Int): Flow<List<WorkDataEntity>>
}