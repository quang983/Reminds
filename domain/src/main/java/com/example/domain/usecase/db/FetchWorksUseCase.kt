package com.example.domain.usecase.db

import com.example.common.base.model.WorkDataEntity
import com.example.domain.repository.BaseUseCase
import com.example.domain.repository.WorkFromTopicRepository
import javax.inject.Inject

class FetchWorksUseCase @Inject constructor(private val topicRepository: WorkFromTopicRepository) :
    BaseUseCase<BaseUseCase.Param, List<WorkDataEntity>> {
    override suspend fun invoke(params: BaseUseCase.Param): List<WorkDataEntity> {
        /*return topicRepository.fetchAllWorkFromTopic()*/
        return mutableListOf<WorkDataEntity>().apply {
            this.add(WorkDataEntity(1, "Công việc 1"))
            this.add(WorkDataEntity(2, "Công việc 2"))
            this.add(WorkDataEntity(3, "Công việc 3"))
            this.add(WorkDataEntity(4, "Công việc 4"))
            this.add(WorkDataEntity(5, "Công việc 5"))
            this.add(WorkDataEntity(6, "Công việc 6"))
        }
    }
}