package com.example.domain.usecase

import com.example.domain.model.MainResponse
import com.example.domain.repository.TryRepository
import javax.inject.Inject

class GetTryApiUseCase @Inject constructor(private val tryRepository: TryRepository){
    suspend operator fun invoke() : MainResponse? {
        return tryRepository.getTryApi()
    }
}
