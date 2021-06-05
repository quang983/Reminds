package com.example.reminds.ui.fragment.tabdaily.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.daily.DailyTaskEntity
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.domain.usecase.db.daily.InsertDailyTaskUseCase
import com.example.reminds.common.BaseViewModel
import com.example.reminds.common.RetrieveDataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDailyTaskViewModel @Inject constructor(private val insertDailyTaskUseCase: InsertDailyTaskUseCase) : BaseViewModel() {
    val stateInsertData: LiveData<RetrieveDataState<DailyTaskEntity>> = MutableLiveData()

    val taskInsertPreview: MutableLiveData<DailyTaskEntity> = MutableLiveData()

    init {
        taskInsertPreview.value = DailyTaskEntity(System.currentTimeMillis(), "", "", System.currentTimeMillis())
    }

    fun insertsDailyTask(taskInsert: DailyTaskEntity) = viewModelScope.launch(Dispatchers.IO + handler) {
        if (checkSatisfy(taskInsert)) {
            insertDailyTaskUseCase.invoke(InsertDailyTaskUseCase.Param(listOf(DailyTaskWithDividerEntity(taskInsert, emptyList()))))
            stateInsertData.postValue(RetrieveDataState.Success(taskInsert))
        } else {
            stateInsertData.postValue(RetrieveDataState.Failure(Throwable()))
        }
    }

    private fun checkSatisfy(taskPreviewInsert: DailyTaskEntity): Boolean {
        if (taskPreviewInsert.name.isBlank()) {
            return false
        }
        if (taskPreviewInsert.type != 0 && taskPreviewInsert.type != 1) {
            return false
        }
        return true
    }
}