package com.example.reminds.ui.fragment.detail

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase
) : BaseViewModel() {
    fun getListWork(idGroup: Long) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup)).collect {
                Log.d("quangtd", "getListWork: ${it.size}")
                listWorkData.postValue(it)
            }
        }
    }

    val listWorkData: LiveData<List<WorkDataEntity>> = MutableLiveData()
}