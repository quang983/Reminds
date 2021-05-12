package com.example.reminds.ui.fragment.tabdaily.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.usecase.db.daily.GetAllDailyTaskFlowUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DailyListViewModel @ViewModelInject constructor(private val dailyTaskFlowUseCase: GetAllDailyTaskFlowUseCase) : BaseViewModel() {
    private val _dailyTaskWithDivider: LiveData<List<DailyTaskWithDividerEntity>> = liveData {
        viewModelScope.launch(handler + Dispatchers.IO) {
            dailyTaskFlowUseCase.invoke(BaseUseCase.Param()).collect {
                emit(it)
            }
        }
    }

    val dailyTaskWithDivider = _dailyTaskWithDivider.switchMapLiveDataEmit { it }
}

