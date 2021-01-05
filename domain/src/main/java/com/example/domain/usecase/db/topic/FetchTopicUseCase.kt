package com.example.domain.usecase.db.topic

import android.util.Log
import com.example.domain.base.BaseUseCase
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<BaseUseCase.Param, Flow<List<TopicGroupEntity>>> {
    override suspend fun invoke(params: BaseUseCase.Param): Flow<List<TopicGroupEntity>> {
        Log.d("connect", "invoke: connect")
        return topicRepository.fetchAllTopicGroups()
    }
}