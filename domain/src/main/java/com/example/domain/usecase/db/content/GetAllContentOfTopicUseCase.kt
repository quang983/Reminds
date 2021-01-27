package com.example.domain.usecase.db.content

import com.example.common.base.model.ContentDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class GetAllContentOfTopicUseCase @Inject constructor(private val topicRepository: WorkFromTopicRepository) :
    BaseUseCase<GetAllContentOfTopicUseCase.Param, List<ContentDataEntity>> {
    override suspend fun invoke(params: Param): List<ContentDataEntity> {
        val works = topicRepository.fetchAllWorkFromTopic(params.idGroup)
        return works.flatMap { it.listContent }
    }

    class Param(val idGroup: Long)
}