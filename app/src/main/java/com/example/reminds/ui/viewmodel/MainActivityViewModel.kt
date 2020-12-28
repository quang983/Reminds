package com.example.reminds.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.reminds.common.BaseViewModel
import com.example.domain.model.MainResponse
import com.example.domain.repository.BaseUseCase
import com.example.domain.usecase.GetTryApiUseCase

class MainActivityViewModel @ViewModelInject constructor(getTryApiUseCase: GetTryApiUseCase) :
    BaseViewModel() {
    private val _getDataApi: LiveData<MainResponse?> = liveDataEmit {
        getTryApiUseCase.invoke(BaseUseCase.Param())
    }


    val getDataApi: LiveData<MainResponse?> = _getDataApi
}