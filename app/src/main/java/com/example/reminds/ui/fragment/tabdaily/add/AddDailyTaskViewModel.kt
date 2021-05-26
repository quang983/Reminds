package com.example.reminds.ui.fragment.tabdaily.add

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.daily.DailyTaskEntity
import com.example.domain.usecase.db.daily.InsertDailyTaskUseCase
import com.example.reminds.common.BaseViewModel
import com.example.reminds.common.RetrieveDataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class AddDailyTaskViewModel @ViewModelInject constructor(private val insertDailyTaskUseCase: InsertDailyTaskUseCase) : BaseViewModel() {
    val stateInsertData: LiveData<RetrieveDataState<Unit?>> = MutableLiveData()

    val taskInsertPreview: MutableLiveData<DailyTaskEntity> = MutableLiveData()

    init {
        taskInsertPreview.value = DailyTaskEntity(System.currentTimeMillis(), "", "", System.currentTimeMillis())
    }

    fun insertsDailyTask(taskInsert: DailyTaskEntity) = viewModelScope.launch(Dispatchers.IO + handler) {
        if (checkSatisfy(taskInsert)) {
            insertDailyTaskUseCase.invoke(InsertDailyTaskUseCase.Param(listOf(taskInsert)))
            stateInsertData.postValue(RetrieveDataState.Success(null))
        } else {
            stateInsertData.postValue(RetrieveDataState.Failure(Throwable()))
        }
    }

    private fun checkSatisfy(taskPreviewInsert: DailyTaskEntity): Boolean {
        if (taskPreviewInsert.name.isBlank()) {
            return false
        }
        return true
    }
}