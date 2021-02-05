package com.example.domain.usecase.db.topic

import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class GetTopicByIdUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<GetTopicByIdUseCase.Param, TopicGroupEntity?> {
    override suspend fun invoke(params: Param): TopicGroupEntity? {
        return topicRepository.getTopicByIdUseCase(params.id)
    }

    class Param(val id: Long)
}