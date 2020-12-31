package com.example.domain.usecase.db.topic

import com.example.domain.base.BaseUseCase
import com.example.domain.model.TopicGroupEntity
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class InsertTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<InsertTopicUseCase.Param, Unit> {
    class Param(val topicList: List<TopicGroupEntity>)

    override suspend fun invoke(params: Param) {
        topicRepository.insertDatas(params.topicList)
    }
}