package com.example.domain.usecase.db.workintopic

import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class GetTotalTaskOfWorkUseCase @Inject constructor(private val topicRepository: WorkFromTopicRepository) :
    BaseUseCase<GetTotalTaskOfWorkUseCase.Param, Int> {
    override suspend fun invoke(params: Param): Int {
        val works = topicRepository.fetchAllWorkFromTopic(params.idGroup)
        return works.sumBy { it.listContent.size }
    }

    class Param(val idGroup: Long)
}