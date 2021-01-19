package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class UpdateListWorkUseCase @Inject constructor(private val workFromTopicRepository: WorkFromTopicRepository) :
    BaseUseCase<UpdateListWorkUseCase.Param, Unit> {
    class Param(val works: List<WorkDataEntity>)

    override suspend fun invoke(params: Param) {
        workFromTopicRepository.updateDatas(params.works)
    }
}