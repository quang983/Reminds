package com.example.domain.usecase.db.daily

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.DailyTaskRepository
import javax.inject.Inject

class InsertDailyTaskUseCase @Inject constructor(val repository: DailyTaskRepository) : BaseUseCase<InsertDailyTaskUseCase.Param, Unit> {
    class Param(val datas: List<DailyTaskWithDividerEntity>)

    override suspend fun invoke(params: Param) {
        return repository.insertDatas(params.datas.map { it.dailyTask })
    }
}