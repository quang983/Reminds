package com.example.reminds.ui.fragment.worktoption

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.topic.FindTopicByIdUseCase
import com.example.domain.usecase.db.workintopic.DeleteWorkUseCase
import com.example.domain.usecase.db.workintopic.GetWorkByIdUseCase
import com.example.domain.usecase.db.workintopic.InsertWorkUseCase
import com.example.domain.usecase.db.workintopic.UpdateWorkUseCase
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.getOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OptionForWorkBSViewModel @ViewModelInject constructor(
    private val workByIdUseCase: GetWorkByIdUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase,
    private val deleteWorkUseCase: DeleteWorkUseCase,
    private val insertWorkUseCase: InsertWorkUseCase,
    private val findTopicByIdUseCase: FindTopicByIdUseCase
) : BaseViewModel() {
    val idGroup: LiveData<Long> = MutableLiveData()

    private val _idWorkLiveData: LiveData<Long> = MutableLiveData()

    val progressUpdateWork: LiveData<Boolean> = MutableLiveData()

    private var _workDataPrepare: WorkDataEntity? = null

    private val _workDataLiveData: MediatorLiveData<WorkDataEntity> = MediatorLiveData<WorkDataEntity>().apply {
        addSource(_idWorkLiveData) {
            viewModelScope.launch(Dispatchers.IO + handler) {
                workByIdUseCase.invoke(GetWorkByIdUseCase.Param(it))?.let {
                    _workDataPrepare = it
                    postValue(it)
                }
            }
        }
        addSource(idGroup) {
            if (_workDataPrepare == null) {
                val workInsert = WorkDataEntity(
                    id = System.currentTimeMillis(),
                    name = "",
                    groupId = idGroup.value ?: 0,
                    listContent = arrayListOf(), doneAll = false
                )
                _workDataPrepare = workInsert
                postValue(workInsert)
            }
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

    fun setNameWorkDataPrepare(name: String) {
        _workDataPrepare?.let {
            it.name = name
            workDataPrepareLiveData.postValue(it)
        }
    }

    fun setDescWorkDataPrepare(desc: String) {
        _workDataPrepare?.let {
            it.description = desc
            workDataPrepareLiveData.postValue(it)
        }
    }

    fun saveWorkIntoDataBase(typeGroup: Int) = viewModelScope.launch(Dispatchers.IO + handler) {
        workDataPrepareLiveData.getOrNull()?.takeIf { it.name.isNotBlank() }?.let {
            insertWorkUseCase.invoke(InsertWorkUseCase.Param(it.copy(), typeGroup)).let {
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