package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSizeWorkByGroupIdUseCase @Inject constructor(private val topicRepository: WorkFromTopicRepository) :
    BaseUseCase<GetSizeWorkByGroupIdUseCase.Param, Int> {
    override suspend fun invoke(params: Param): Int {
        return topicRepository.fetchAllWorkFromTopic(params.idGroup).size
    }

    class Param(val idGroup: Long)
}