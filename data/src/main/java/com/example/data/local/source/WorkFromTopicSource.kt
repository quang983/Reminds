package com.example.data.local.source

import com.example.common.base.model.WorkDataEntity
import kotlinx.coroutines.flow.Flow

interface WorkFromTopicSource :BaseSource<WorkDataEntity>{
    suspend fun fetchAllFlow(idGroup: Long): Flow<List<WorkDataEntity>>

    suspend fun fetchAll(idGroup: Long): List<WorkDataEntity>

    suspend fun getWorkById(idWork : Long) : WorkDataEntity?

    suspend fun getAllWorkDependState(state : Int) : Flow<List<WorkDataEntity>>
}
