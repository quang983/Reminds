package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class UpdateWorkUseCase @Inject constructor(private val workFromTopicRepository: WorkFromTopicRepository) :
    BaseUseCase<UpdateWorkUseCase.Param, Unit> {
    class Param(val work: WorkDataEntity)

    override suspend fun invoke(params: Param) {
        workFromTopicRepository.updateData(params.work)
    }
}