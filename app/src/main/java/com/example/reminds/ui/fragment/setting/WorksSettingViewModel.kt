package com.example.reminds.ui.fragment.setting

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.usecase.db.topic.GetTopicByIdUseCase
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.flow.collect

class WorksSettingViewModel @ViewModelInject constructor(private val getTopicByIdUseCase: GetTopicByIdUseCase) : BaseViewModel() {
    var optionSelected: Int = TopicGroupEntity.SHOW_ALL_WORKS
    private val _idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    val getTopicByIdLiveData :LiveData<Int> = _idGroup.switchMapLiveData  {
        getTopicByIdUseCase.invoke(GetTopicByIdUseCase.Param(_idGroup.value ?: return@switchMapLiveData)).collect {
            emit(it.optionSelected)
        }
    }

    fun setIdGroup(idGroup: Long) {
        _idGroup.postValue(idGroup)
    }
}