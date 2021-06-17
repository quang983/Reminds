package com.example.domain.usecase.db.daily.done

import com.example.common.base.model.daily.DailyDivideTaskDoneEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.repository.DailyDivideDoneRepository
import javax.inject.Inject

class InsertOrUpdateDailyTaskDoneUseCase @Inject constructor(val doneRepository: DailyDivideDoneRepository) : BaseUseCase<InsertOrUpdateDailyTaskDoneUseCase.Param, Unit> {

    override suspend fun invoke(params: Param) {
        doneRepository.insertDatas(params.dailyList)
    }

    class Param(val dailyList: List<DailyDivideTaskDoneEntity>)
}
