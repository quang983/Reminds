package com.example.reminds.ui.sharedviewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.UpdateWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FocusActivityViewModel @ViewModelInject constructor(private val updateWorkUseCase: UpdateWorkUseCase) : BaseViewModel() {
    val itemWorkSelected: MutableLiveData<WorkDataEntity?> = MutableLiveData()

    fun doneAllInWork() = viewModelScope.launch(Dispatchers.IO + handler) {
        itemWorkSelected.value?.let {
            it.doneAll = true
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
            itemWorkSelected.postValue(null)
        }
    }
}
