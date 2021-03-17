package com.example.domain.usecase.db.topic

import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchTopicFlowUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<FetchTopicFlowUseCase.Param, Flow<List<TopicGroupEntity>>> {
    override suspend fun invoke(params: Param): Flow<List<TopicGroupEntity>> {
        return topicRepository.fetchAllTopicFlowGroups(params.typeGroup)
    }

    data class Param(val typeGroup: Int)
}