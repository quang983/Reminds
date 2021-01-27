package com.example.domain.usecase.db.topic

import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import java.util.*
import javax.inject.Inject

class GetTodayTopicUseCase @Inject constructor(private val topicRepository: TopicRepository) :
    BaseUseCase<BaseUseCase.Param, TopicGroupEntity?> {
    override suspend fun invoke(params: BaseUseCase.Param): TopicGroupEntity? {
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR, 0)
        val time1 = date.timeInMillis
        val date2 = Calendar.getInstance()
        date2.set(Calendar.HOUR, 23)
        date2.set(Calendar.MINUTE, 59)
        val time2 = date2.timeInMillis
        topicRepository.getTodayTopicUseCase(time1, time2).let { }
        return null
    }
}