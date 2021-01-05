package com.example.domain.usecase.db.topic

import com.example.domain.base.BaseUseCase
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class DeleteTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<DeleteTopicUseCase.Param, Unit> {
    class Param(val topicList: List<TopicGroupEntity>)

    override suspend fun invoke(params: Param) {
        topicRepository.deleteDatas(params.topicList)
    }
}