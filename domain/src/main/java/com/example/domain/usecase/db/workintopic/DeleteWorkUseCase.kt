package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class DeleteWorkUseCase @Inject constructor(private val workFromTopicRepository: WorkFromTopicRepository) :
    BaseUseCase<DeleteWorkUseCase.Param, Unit> {
    class Param(val work: WorkDataEntity?)

    override suspend fun invoke(params: Param) {
        if(params.work == null){

        }else{
            workFromTopicRepository.deleteDatas(listOf(params.work))
        }
    }
}