package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllWorkFlowUseCase @Inject constructor(private val topicRepository: WorkFromTopicRepository) :
    BaseUseCase<GetAllWorkFlowUseCase.Param, Flow<List<WorkDataEntity>>> {
    override suspend fun invoke(params: Param): Flow<List<WorkDataEntity>> {
        return topicRepository.getAllWorkDependState(params.state)
    }

    class Param(val state: Int)
}