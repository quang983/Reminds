package com.example.reminds.ui.fragment.worktoption

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.DeleteWorkUseCase
import com.example.domain.usecase.db.workintopic.GetWorkByIdUseCase
import com.example.domain.usecase.db.workintopic.UpdateWorkUseCase
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.getOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OptionForWorkBSViewModel @ViewModelInject constructor(
    private val workByIdUseCase: GetWorkByIdUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase,
    private val deleteWorkUseCase: DeleteWorkUseCase
) : BaseViewModel() {
    private val _idWorkLiveData: LiveData<Long> = MutableLiveData()

    val progressUpdateWork: LiveData<Boolean> = MutableLiveData()

    private var _workDataPrepare: WorkDataEntity? = null

    private val _workDataLiveData: LiveData<WorkDataEntity> = _idWorkLiveData.switchMapLiveData { it ->
        workByIdUseCase.invoke(GetWorkByIdUseCase.Param(it))?.let {
            _workDataPrepare = it
            emit(it)
        }
    }

    val workDataPrepareLiveData: LiveData<WorkDataEntity> = _workDataLiveData.switchMapLiveDataEmit {
        it
    }

    fun setIdWork(idWork: Long) = _idWorkLiveData.postValue(idWork)

    fun setTimerWorkDataPrepare(timer: Long) {
        _workDataPrepare?.let {
            it.timerReminder = timer
            workDataPrepareLiveData.postValue(it)
        }

    }

    fun setHashTagWorkDataPrepare() {
        _workDataPrepare?.let {
            it.hashTag = !it.hashTag
            workDataPrepareLiveData.postValue(it)
        }
    }

    fun setNameWorkDataPrepare(name: String) {
        _workDataPrepare?.let {
            it.name = name
            workDataPrepareLiveData.postValue(it)
        }
    }

    fun saveWorkIntoDataBase() = viewModelScope.launch(Dispatchers.IO + handler) {
        workDataPrepareLiveData.getOrNull()?.takeIf { it.name.isNotBlank() }?.let {
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it.copy())).let {
                progressUpdateWork.postValue(true)
            }
        } ?: apply {
            progressUpdateWork.postValue(false)
        }
    }

    fun deleteWork() = viewModelScope.launch(handler + Dispatchers.IO) {
        deleteWorkUseCase.invoke(DeleteWorkUseCase.Param(_workDataLiveData.value))
    }

}