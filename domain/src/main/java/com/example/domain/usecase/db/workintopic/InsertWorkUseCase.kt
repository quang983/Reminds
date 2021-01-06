package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class InsertWorkUseCase @Inject constructor(private val workFromTopicRepository: WorkFromTopicRepository) :
    BaseUseCase<InsertWorkUseCase.Param, Long> {
    class Param(val content: WorkDataEntity)

    override suspend fun invoke(params: Param): Long {
        return workFromTopicRepository.insertData(params.content)
    }
}