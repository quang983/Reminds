package com.example.domain.usecase.db.daily

import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.DailyDivideDoneRepository
import com.example.domain.repository.DailyTaskRepository
import javax.inject.Inject

class UpdateDailyTaskUseCase @Inject constructor(val taskRepository: DailyTaskRepository, val doneRepository: DailyDivideDoneRepository) : BaseUseCase<UpdateDailyTaskUseCase.Param, Unit> {

    override suspend fun invoke(params: Param) {

        taskRepository.updateDatas(listOf(params.taskUpdate.dailyTask))
        doneRepository.insertDatas(params.taskUpdate.dailyList)
    }

    class Param(val taskUpdate: DailyTaskWithDividerEntity)
}
