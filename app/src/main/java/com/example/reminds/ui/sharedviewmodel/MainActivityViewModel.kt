package com.example.reminds.ui.sharedviewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.domain.model.MainResponse
import com.example.domain.repository.BaseUseCase
import com.example.domain.usecase.remote.GetTryApiUseCase
import net.citigo.kiotviet.pos.fnb.ui.viewmodels.BaseViewModel

class MainActivityViewModel @ViewModelInject constructor(getTryApiUseCase: GetTryApiUseCase) :
    BaseViewModel() {
    private val _getDataApi: LiveData<MainResponse?> = liveDataEmit {
        getTryApiUseCase.invoke(BaseUseCase.Param())
    }


    val getDataApi: LiveData<MainResponse?> = _getDataApi
}