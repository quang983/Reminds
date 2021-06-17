 package com.example.domain.usecase.remote

import com.example.domain.model.MainResponse
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.TryRepository
import javax.inject.Inject

class GetTryApiUseCase @Inject constructor(private val tryRepository: TryRepository) :
    BaseUseCase<BaseUseCase.Param, MainResponse?> {
    override suspend fun invoke(params: BaseUseCase.Param): MainResponse? {
        return kotlin.runCatching { tryRepository.getTryApi() }.getOrNull()
    }
}
