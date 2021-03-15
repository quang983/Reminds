package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class GetWorkByIdUseCase @Inject constructor(private val workRepository: WorkFromTopicRepository) :
    BaseUseCase<GetWorkByIdUseCase.Param, WorkDataEntity?> {
    override suspend fun invoke(params: Param): WorkDataEntity? {
        requireNotNull(params.id)

        return workRepository.getWorkFromTopicById(params.id)
    }

    class Param(val id: Long)
}
