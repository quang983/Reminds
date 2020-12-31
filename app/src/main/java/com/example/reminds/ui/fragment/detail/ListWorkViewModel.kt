package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import com.example.domain.base.BaseUseCase
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.reminds.common.BaseViewModel

class ListWorkViewModel @ViewModelInject constructor(private val fetchWorksUseCase: FetchWorksUseCase) :
    BaseViewModel() {
    val listWorkData = liveDataEmit {
        fetchWorksUseCase.invoke(BaseUseCase.Param())
    }
}