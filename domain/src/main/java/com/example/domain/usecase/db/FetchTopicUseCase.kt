package com.example.domain.usecase.db

import com.example.domain.model.TopicGroupEntity
import com.example.domain.repository.BaseUseCase
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class FetchTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) : BaseUseCase<BaseUseCase.Param, List<TopicGroupEntity>>{
    override suspend fun invoke(params: BaseUseCase.Param): List<TopicGroupEntity> {
        return topicRepository.fetchAllTopicGroups()
    }
}