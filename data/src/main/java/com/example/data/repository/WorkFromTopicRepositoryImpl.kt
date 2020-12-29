package com.example.data.repository

import com.example.common.base.model.WorkDataEntity
import com.example.data.local.source.WorkFromTopicSource
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class WorkFromTopicRepositoryImpl @Inject constructor(private val workFromTopicSource: WorkFromTopicSource) :
    WorkFromTopicRepository {
    override suspend fun fetchAllWorkFromTopic(): List<WorkDataEntity> {
        return workFromTopicSource.fetchAll()
    }
}