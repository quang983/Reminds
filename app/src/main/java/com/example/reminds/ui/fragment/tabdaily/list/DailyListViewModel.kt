package com.example.reminds.ui.fragment.tabdaily.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.usecase.db.daily.GetAllDailyTaskFlowUseCase
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyListViewModel @Inject constructor(private val dailyTaskFlowUseCase: GetAllDailyTaskFlowUseCase) : BaseViewModel() {
    private val _dailyTaskWithDivider: MediatorLiveData<List<DailyTaskWithDividerEntity>> = MediatorLiveData<List<DailyTaskWithDividerEntity>>().apply {
        viewModelScope.launch(handler + Dispatchers.IO) {
            dailyTaskFlowUseCase.invoke(BaseUseCase.Param()).collect {
                postValue(it)
            }
        }
    }

    val dailyTaskWithDivider: LiveData<List<DailyTaskWithDividerEntity>> = _dailyTaskWithDivider.switchMapLiveDataEmit {
        it
    }
}

