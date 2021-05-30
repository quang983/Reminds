package com.example.data.repository

import com.example.common.base.model.daily.DailyDivideTaskDoneEntity
import com.example.data.local.source.DivideDoneSource
import com.example.domain.repository.DailyDivideDoneRepository
import javax.inject.Inject

class DailyDivideDoneRepositoryImpl @Inject constructor(val sourceDivider: DivideDoneSource) : BaseRepositoryImpl<DailyDivideTaskDoneEntity>(sourceDivider), DailyDivideDoneRepository