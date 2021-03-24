package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class InsertWorkUseCase @Inject constructor(
    private val workFromTopicRepository: WorkFromTopicRepository,
    private val topicRepository: TopicRepository,
    private val getSizeWorkByGroupIdUseCase: GetSizeWorkByGroupIdUseCase
) :
    BaseUseCase<InsertWorkUseCase.Param, Long> {

    override suspend fun invoke(params: Param): Long {
        topicRepository.findTopicByIdUseCase(params.work.groupId).apply {
            if (this == null) {
                topicRepository.insertData(TopicGroupEntity(params.work.groupId, "", typeTopic = params.typeTopic))
            }
        }
        return workFromTopicRepository.insertData(params.work.apply {
            val size = getSizeWorkByGroupIdUseCase.invoke(GetSizeWorkByGroupIdUseCase.Param(params.work.groupId))
            this.stt = size
        })
    }

    class Param(val work: WorkDataEntity, val typeTopic: Int)
}