package com.example.reminds.ui.fragment.tabdaily.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.daily.DailyDivideTaskDoneEntity
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.usecase.db.daily.GetDailyTaskByIdUseCase
import com.example.domain.usecase.db.daily.UpdateDailyTaskUseCase
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.getOrNull
import com.example.reminds.utils.toArrayList
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DailyTaskDetailViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val getDailyTaskByIdUseCase: GetDailyTaskByIdUseCase,
    private val updateDailyTaskUseCase: UpdateDailyTaskUseCase
) : BaseViewModel() {
    private val _getDetailDailyTask: LiveData<DailyTaskWithDividerEntity> = liveData {
        getDailyTaskByIdUseCase.invoke(GetDailyTaskByIdUseCase.Param(id)).collect {
            emit(it)
        }
    }

    val getDetailDailyTask = _getDetailDailyTask.switchMapLiveDataEmit {
        it
    }

    val showCheckInLiveData: LiveData<Boolean> = _getDetailDailyTask.switchMapLiveDataEmit { it ->
        it.dailyList
            .map {
                it.doneTime
            }app/src/main/java/com/example/reminds/ui/fragment/tabdaily/add/AddDailyTaskFragment.kt
            .any {
                TimestampUtils.compareDate(it, System.currentTimeMillis())
            }
    }

    init {
        viewModelScope.launch(Dispatchers.IO + handler) {
            getDailyTaskByIdUseCase.invoke(GetDailyTaskByIdUseCase.Param(id)).collect {
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

    class Factory(
        private val assistedFactory: DailyDetailViewModelAssistedFactory,
        private val id: Long
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return assistedFactory.create(id) as T
        }

    }
}

@AssistedFactory
interface DailyDetailViewModelAssistedFactory {
    fun create(id: Long): DailyTaskDetailViewModel
}