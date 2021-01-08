package com.example.domain.usecase.db.content

import com.example.common.base.model.ContentDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.ContentRepository
import javax.inject.Inject

class UpdateContentsUseCase @Inject constructor(private val contentRepository: ContentRepository) :
    BaseUseCase<UpdateContentsUseCase.Param, Unit> {
    class Param(val contents: List<ContentDataEntity>)

    override suspend fun invoke(params: Param) {
        contentRepository.updateDatas(params.contents)
    }
}