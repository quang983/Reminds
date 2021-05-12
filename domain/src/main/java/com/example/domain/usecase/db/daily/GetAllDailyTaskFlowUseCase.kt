package com.example.domain.usecase.db.daily

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.DailyTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllDailyTaskFlowUseCase @Inject constructor(private val repository: DailyTaskRepository) :
    BaseUseCase<BaseUseCase.Param, Flow<List<DailyTaskWithDividerEntity>>> {
    override suspend fun invoke(params: BaseUseCase.Param): Flow<List<DailyTaskWithDividerEntity>> {
        return repository.getAllDataFlow()
    }
}