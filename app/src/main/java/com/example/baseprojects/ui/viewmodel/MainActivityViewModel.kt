package com.example.baseprojects.ui.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.baseprojects.common.BaseViewModel
import com.example.baseprojects.common.Constants
import com.example.domain.model.MainResponse
import com.example.domain.repository.BaseUseCase
import com.example.domain.usecase.GetTryApiUseCase

class MainActivityViewModel @ViewModelInject constructor(getTryApiUseCase: GetTryApiUseCase) :
    BaseViewModel() {
    private val _getDataApi: LiveData<MainResponse?> = liveDataEmit {
        Log.d(Constants.TAG, "_getDataApi")
            getTryApiUseCase.invoke(BaseUseCase.Param())
    }


    val getDataApi: LiveData<MainResponse?> = _getDataApi.switchMapLiveDataEmit {
        it
    }
}