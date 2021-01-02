package com.example.framework.source

import com.example.common.base.model.WorkDataEntity
import com.example.data.local.source.WorkFromTopicSource
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import javax.inject.Inject

class WorkFromTopicSourceImpl @Inject constructor(
    private val dao: LocalWorkFromTopicDao
) : WorkFromTopicSource {
    override suspend fun fetchAll(): List<WorkDataEntity> {
        return dao.fetchWorkFromTopicData().map {
            it.toEntity(it)
        }
    }
}
