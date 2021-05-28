package com.example.reminds.ui.fragment.tabdaily.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.daily.DailyDivideTaskDoneEntity
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.usecase.db.daily.GetDailyTaskByIdUseCase
import com.example.domain.usecase.db.daily.UpdateDailyTaskUseCase
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.getOrNull
import com.example.reminds.utils.toArrayList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DailyTaskDetailViewModel @ViewModelInject constructor(
    private val idTask: Long,
    private val getDailyTaskByIdUseCase: GetDailyTaskByIdUseCase,
    private val updateDailyTaskUseCase: UpdateDailyTaskUseCase
) : BaseViewModel() {
    private val _getDetailDailyTask: LiveData<DailyTaskWithDividerEntity> = MutableLiveData()
    val getDetailDailyTask = _getDetailDailyTask.switchMapLiveDataEmit {
        it
    }

    val showCheckInLiveData = _getDetailDailyTask.switchMapLiveDataEmit { it ->
        (it.dailyList.map { it.doneTime }.any { TimestampUtils.compareDate(it, System.currentTimeMillis()) })
    }

    init {
        viewModelScope.launch(Dispatchers.IO + handler) {
            getDailyTaskByIdUseCase.invoke(GetDailyTaskByIdUseCase.Param(idTask)).collect {
                _getDetailDailyTask.postValue(it)
            }
        }
    }

    fun updateDividerInDailyTask() = viewModelScope.launch(Dispatchers.IO + handler) {
        getDetailDailyTask.getOrNull()?.apply {
            val taskDone = DailyDivideTaskDoneEntity(System.currentTimeMillis(), this.dailyTask.id, "", System.currentTimeMillis())
            this.dailyList.toArrayList().add(taskDone)
            updateDailyTaskUseCase.invoke(UpdateDailyTaskUseCase.Param(this))
        }
    }
}