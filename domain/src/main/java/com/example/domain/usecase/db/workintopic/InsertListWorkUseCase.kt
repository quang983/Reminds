package com.example.domain.usecase.db.workintopic

import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TopicRepository
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class InsertListWorkUseCase @Inject
constructor(
    private val workFromTopicRepository: WorkFromTopicRepository,
    private val topicRepository: TopicRepository,
    private val getSizeWorkByGroupIdUseCase: GetSizeWorkByGroupIdUseCase
) :
    BaseUseCase<InsertListWorkUseCase.Param, Unit> {
    class Param(val works: List<WorkDataEntity>, val typeTopic: Int)

    override suspend fun invoke(params: Param) {
        params.works.getOrNull(0)?.groupId?.let { it ->
            topicRepository.findTopicByIdUseCase(it).apply {
                if (this == null) {
                    topicRepository.insertData(TopicGroupEntity(it, "", typeTopic = params.typeTopic))
                }
            }
            workFromTopicRepository.insertDatas(params.works.map {
                it.apply {
                    val size = getSizeWorkByGroupIdUseCase.invoke(GetSizeWorkByGroupIdUseCase.Param(this.groupId))
                    this.stt = size
                }
            })
        }
    }
}