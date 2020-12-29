package com.example.domain.usecase.db

import com.example.domain.model.TopicGroupEntity
import com.example.domain.repository.BaseUseCase
import com.example.domain.repository.TopicRepository
import javax.inject.Inject

class FetchTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) : BaseUseCase<BaseUseCase.Param, List<TopicGroupEntity>>{
    override suspend fun invoke(params: BaseUseCase.Param): List<TopicGroupEntity> {
        return topicRepository.fetchAllTopicGroups().toMutableList().apply {
            this.add(TopicGroupEntity(1,"Chủ đề 1"))
            this.add(TopicGroupEntity(2,"Chủ đề 2"))
            this.add(TopicGroupEntity(3,"Chủ đề 3"))
            this.add(TopicGroupEntity(4,"Chủ đề 4"))
            this.add(TopicGroupEntity(5,"Chủ đề 5"))
            this.add(TopicGroupEntity(6,"Chủ đề 6"))
        }
    }
}