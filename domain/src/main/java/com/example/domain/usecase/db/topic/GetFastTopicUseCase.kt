package com.example.domain.usecase.db.topic

import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class GetFastTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<BaseUseCase.Param, TopicGroupEntity?> {
    override suspend fun invoke(params: BaseUseCase.Param): TopicGroupEntity? {
        return topicRepository.getFastTopicUseCase()
    }
}