package com.example.domain.usecase.db.topic

import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopicByIdUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<GetTopicByIdUseCase.Param, Flow<TopicGroupEntity?>> {
    override suspend fun invoke(params: Param): Flow<TopicGroupEntity?> {
        return topicRepository.getTopicByIdUseCase(params.id)
    }

    class Param(val id: Long)
}