package com.example.data.repository

import com.example.common.base.model.WorkDataEntity
import com.example.data.local.source.WorkFromTopicSource
import com.example.domain.repository.WorkFromTopicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkFromTopicRepositoryImpl @Inject constructor(private val workFromTopicSource: WorkFromTopicSource) :
    WorkFromTopicRepository {
    override suspend fun fetchAllWorkFromTopicFlow(idGroup: Long): Flow<List<WorkDataEntity>> {
        return workFromTopicSource.fetchAllFlow(idGroup)
    }

    override suspend fun fetchAllWorkFromTopic(idGroup: Long): List<WorkDataEntity> {
        return workFromTopicSource.fetchAll(idGroup)
    }

    override suspend fun getWorkFromTopicById(idWork: Long): WorkDataEntity? {
        return workFromTopicSource.getWorkById(idWork)
    }

    override suspend fun getAllWorkDependState(state: Int): Flow<List<WorkDataEntity>> {
        return workFromTopicSource.getAllWorkDependState(state)
    }

    override suspend fun insertData(data: WorkDataEntity): Long {
        return workFromTopicSource.insert(data)
    }

    override suspend fun insertDatas(datas: List<WorkDataEntity>) {
        workFromTopicSource.inserts(datas)
    }

    override suspend fun updateData(data: WorkDataEntity) {
        workFromTopicSource.update(data)
    }

    override suspend fun updateDatas(datas: List<WorkDataEntity>) {
        workFromTopicSource.updates(datas)
    }

    override suspend fun deleteDatas(datas: List<WorkDataEntity>) {
        workFromTopicSource.deletes(datas)
    }
}