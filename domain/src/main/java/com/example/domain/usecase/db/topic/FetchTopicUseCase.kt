package com.example.domain.usecase.db.topic

import com.example.domain.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class FetchTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<BaseUseCase.Param, List<TopicGroupEntity>> {
    override suspend fun invoke(params: BaseUseCase.Param): List<TopicGroupEntity> {
        /* return topicRepository.fetchAllTopicGroups() */
        return mutableListOf<TopicGroupEntity>().apply {
            add(TopicGroupEntity(1, "Chủ đề 1"))
            add(TopicGroupEntity(2, "Chủ đề 2"))
            add(TopicGroupEntity(3, "Chủ đề 3"))
            add(TopicGroupEntity(4, "Chủ đề 4"))
            add(TopicGroupEntity(5, "Chủ đề 5"))
            add(TopicGroupEntity(6, "Chủ đề 6"))
        }
    }
}