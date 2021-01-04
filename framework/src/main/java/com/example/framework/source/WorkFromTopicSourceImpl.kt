package com.example.framework.source

import com.example.common.base.model.WorkDataEntity
import com.example.data.local.source.WorkFromTopicSource
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkFromTopicSourceImpl @Inject constructor(
    private val dao: LocalWorkFromTopicDao
) : WorkFromTopicSource {
    override suspend fun fetchAll(idGroup: Long): Flow<List<WorkDataEntity>> {
        return dao.fetchWorkFromTopicData(idGroup).map { it ->
            it.listWork.map {
                WorkDataEntity(
                    it.id, it.name
                )
            }
        }.conflate()
    }
}
