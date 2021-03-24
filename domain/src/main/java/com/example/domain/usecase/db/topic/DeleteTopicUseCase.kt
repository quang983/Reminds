package com.example.domain.usecase.db.topic

import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class DeleteTopicUseCase @Inject constructor(
    private val topicRepository: TopicRepository,
    private val workFromTopicRepository: WorkFromTopicRepository
) :
    BaseUseCase<DeleteTopicUseCase.Param, Unit> {
    class Param(val topicList: List<TopicGroupEntity>)

    override suspend fun invoke(params: Param) {
        topicRepository.deleteDatas(params.topicList)
        workFromTopicRepository.deleteDatas(
            workFromTopicRepository
                .fetchAllWorkFromTopic(params.topicList.getOrNull(0)?.id ?: return)
        )
    }
}