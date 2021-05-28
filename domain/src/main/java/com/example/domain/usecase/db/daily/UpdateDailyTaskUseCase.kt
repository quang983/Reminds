package com.example.domain.usecase.db.daily

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.DailyTaskRepository
import javax.inject.Inject

class UpdateDailyTaskUseCase @Inject constructor(val repository: DailyTaskRepository) : BaseUseCase<UpdateDailyTaskUseCase.Param,Unit> {

    override suspend fun invoke(params: Param) {
        repository.updateDatas(listOf(params.taskUpdate))
    }

    class Param(val taskUpdate: DailyTaskWithDividerEntity)
}
