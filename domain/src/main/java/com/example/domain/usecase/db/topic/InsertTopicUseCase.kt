package com.example.domain.usecase.db.topic

import com.example.domain.base.BaseUseCase
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class InsertTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<InsertTopicUseCase.Param, Long> {
    class Param(val topic: TopicGroupEntity)

    override suspend fun invoke(params: Param) : Long {
        return topicRepository.insertData(params.topic)
    }
}