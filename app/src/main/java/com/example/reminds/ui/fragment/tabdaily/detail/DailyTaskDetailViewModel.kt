package com.example.reminds.ui.fragment.tabdaily.detail

import androidx.lifecycle.*
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class DailyTaskDetailViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
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
            getDailyTaskByIdUseCase.invoke(GetDailyTaskByIdUseCase.Param(0)).collect {
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