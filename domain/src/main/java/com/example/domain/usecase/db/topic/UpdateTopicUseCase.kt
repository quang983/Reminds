package com.example.domain.usecase.db.topic

import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class UpdateTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<UpdateTopicUseCase.Param, Unit> {
    class Param(val topic: TopicGroupEntity)

    override suspend fun invoke(params: Param) {
        topicRepository.updateDatas(listOf(params.topic))
    }
}