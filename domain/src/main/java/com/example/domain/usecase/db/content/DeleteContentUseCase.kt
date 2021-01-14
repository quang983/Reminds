package com.example.domain.usecase.db.content

import com.example.common.base.model.ContentDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.ContentRepository
import javax.inject.Inject

class DeleteContentUseCase  @Inject constructor(private val contentRepository: ContentRepository) :
    BaseUseCase<DeleteContentUseCase.Param, Unit> {
    class Param(val topicList: List<ContentDataEntity>)

    override suspend fun invoke(params: Param) {
        contentRepository.deleteDatas(params.topicList)
    }
}