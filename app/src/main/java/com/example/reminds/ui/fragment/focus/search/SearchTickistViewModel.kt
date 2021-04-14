package com.example.reminds.ui.fragment.focus.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.GetAllWorkFlowUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.flow.collect

class SearchTickistViewModel @ViewModelInject constructor(getAllWorkFlowUseCase: GetAllWorkFlowUseCase) : BaseViewModel() {
    private val _getAllWorkLiveData: LiveData<List<WorkDataEntity>> = liveData {
        getAllWorkFlowUseCase.invoke(GetAllWorkFlowUseCase.Param(0)).collect {
            emit(it)
        }
    }

    val getAllWorkLiveData = _getAllWorkLiveData.switchMapLiveDataEmit {
        it
    }
}