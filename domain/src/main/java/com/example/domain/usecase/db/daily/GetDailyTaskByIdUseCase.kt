package com.example.domain.usecase.db.daily

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.DailyTaskRepository
import kotlinx.coroutines.flow.Flow

class GetDailyTaskByIdUseCase(val repository: DailyTaskRepository) : BaseUseCase<GetDailyTaskByIdUseCase.Param, Flow<DailyTaskWithDividerEntity>> {
    class Param(val id: Long)

    override suspend fun invoke(params: Param): Flow<DailyTaskWithDividerEntity> {
        return repository.getDetailById(params.id)
    }
}