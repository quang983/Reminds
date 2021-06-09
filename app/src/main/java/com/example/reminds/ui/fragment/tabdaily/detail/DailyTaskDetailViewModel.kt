package com.example.reminds.ui.fragment.tabdaily.detail

import android.util.Log
import androidx.lifecycle.*
import com.example.common.base.model.daily.DailyDivideTaskDoneEntity
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.usecase.db.daily.GetDailyTaskByIdUseCase
import com.example.domain.usecase.db.daily.UpdateDailyTaskUseCase
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.fromISO8601
import com.example.reminds.utils.getOrNull
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DailyTaskDetailViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val getDailyTaskByIdUseCase: GetDailyTaskByIdUseCase,
    private val updateDailyTaskUseCase: UpdateDailyTaskUseCase
) : BaseViewModel() {
    var localDateChecked: LiveData<LocalDate> = liveData {
        emit(LocalDate.now())
    }

    private val _getDetailDailyTask: LiveData<DailyTaskWithDividerEntity> = liveData {
        getDailyTaskByIdUseCase.invoke(GetDailyTaskByIdUseCase.Param(id)).distinctUntilChanged().collect {
            emit(it)
        }
    }

    val getDetailDailyTask = _getDetailDailyTask.switchMapLiveDataEmit {
        it
    }

    val showCheckInLiveData: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        combineSources(localDateChecked, getDetailDailyTask) {
            val result = getDetailDailyTask.getOrNull()?.dailyList.takeIf { it?.isNotEmpty() == true }?.map {
                it.doneTime
            }?.any {
                val cal = Calendar.getInstance()
                cal.timeInMillis = it
                cal[Calendar.YEAR] == localDateChecked.getOrNull()?.year &&
                        cal[Calendar.DAY_OF_YEAR] == localDateChecked.getOrNull()?.dayOfYear
            } ?: false
            postValue(!result)
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
            val localDate = localDateChecked.value

            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formattedString = localDate?.format(formatter)
            val taskDone = DailyDivideTaskDoneEntity(System.currentTimeMillis(), this.dailyTask.id, "", formattedString?.fromISO8601() ?: System.currentTimeMillis())

            (this.dailyList as? ArrayList)?.add(taskDone)
            this.dailyTask.name = "aaa"
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