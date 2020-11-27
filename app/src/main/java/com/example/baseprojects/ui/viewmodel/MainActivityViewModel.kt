package com.example.baseprojects.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.baseprojects.common.BaseViewModel
import com.example.domain.model.MainResponse
import com.example.domain.repository.TryRepository
import com.example.domain.usecase.GetTryApiUseCase

class MainActivityViewModel @ViewModelInject constructor(getTryApiUseCase: GetTryApiUseCase) :
    BaseViewModel() {
    private val _getDataApi: LiveData<MainResponse?> = liveDataEmit {
        getTryApiUseCase.invoke()
    }

    val getDataApi : LiveData<MainResponse?>  = _getDataApi
}