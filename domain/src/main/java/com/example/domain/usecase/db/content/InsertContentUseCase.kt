package com.example.domain.usecase.db.content

import com.example.common.base.model.ContentDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.ContentRepository
import javax.inject.Inject

class InsertContentUseCase @Inject constructor(private val contentRepository: ContentRepository) :
    BaseUseCase<InsertContentUseCase.Param, Long> {
    class Param(val content: ContentDataEntity)

    override suspend fun invoke(params: Param): Long {
        return contentRepository.insertData(params.content)
    }
}