package com.example.reminds.ui.sharedviewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.common.BaseViewModel

class FocusActivityViewModel @ViewModelInject constructor() : BaseViewModel() {
    val itemWorkSelected: MutableLiveData<WorkDataEntity?> = MutableLiveData()
}