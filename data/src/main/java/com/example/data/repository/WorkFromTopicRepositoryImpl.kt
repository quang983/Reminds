package com.example.data.repository

import com.example.common.base.model.WorkDataEntity
import com.example.data.local.source.WorkFromTopicSource
import com.example.domain.repository.WorkFromTopicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkFromTopicRepositoryImpl @Inject constructor(private val workFromTopicSource: WorkFromTopicSource) :
    WorkFromTopicRepository {
    override suspend fun fetchAllWorkFromTopic(idGroup: Long): Flow<List<WorkDataEntity>> {
        return workFromTopicSource.fetchAll(idGroup)
    }

    override suspend fun insertData(data: WorkDataEntity): Long {
        return workFromTopicSource.insert(data)
    }

    override suspend fun insertDatas(datas: List<WorkDataEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDatas(datas: List<WorkDataEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDatas(datas: List<WorkDataEntity>) {
        TODO("Not yet implemented")
    }
}