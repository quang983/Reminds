package com.example.reminds.ui.fragment.focus.search

import androidx.lifecycle.LiveData
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.GetAllWorkFlowUseCase
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class SearchTickistViewModel @Inject constructor(getAllWorkFlowUseCase: GetAllWorkFlowUseCase) : BaseViewModel() {
    private val _getAllWorkLiveData: LiveData<List<WorkDataEntity>> = liveData {
        getAllWorkFlowUseCase.invoke(GetAllWorkFlowUseCase.Param(0)).collect {
            emit(it)
        }
    }

    val getAllWorkLiveData = _getAllWorkLiveData.switchMapLiveDataEmit {
        it
    }
}