package com.example.reminds.ui.sharedviewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.domain.model.MainResponse
import com.example.domain.base.BaseUseCase
import com.example.domain.usecase.remote.GetTryApiUseCase
import com.example.reminds.common.BaseViewModel

class MainActivityViewModel @ViewModelInject constructor(getTryApiUseCase: GetTryApiUseCase) :
    BaseViewModel() {
    private val _getDataApi: LiveData<MainResponse?> = liveDataEmit {
        getTryApiUseCase.invoke(BaseUseCase.Param())
    }


    val getDataApi: LiveData<MainResponse?> = _getDataApi
}