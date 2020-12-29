package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import com.example.domain.repository.BaseUseCase
import com.example.domain.usecase.db.FetchWorksUseCase
import net.citigo.kiotviet.pos.fnb.ui.viewmodels.BaseViewModel

class ListWorkViewModel @ViewModelInject constructor(private val fetchWorksUseCase: FetchWorksUseCase) :
    BaseViewModel() {
    val listWorkData = liveDataEmit {
        fetchWorksUseCase.invoke(BaseUseCase.Param())
    }
}