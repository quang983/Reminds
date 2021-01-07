package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class InsertListWorkUseCase @Inject
constructor(private val workFromTopicRepository: WorkFromTopicRepository) :
    BaseUseCase<InsertListWorkUseCase.Param, Unit> {
    class Param(val work: List<WorkDataEntity>)

    override suspend fun invoke(params: Param) {
        return workFromTopicRepository.insertDatas(params.work)
    }
}