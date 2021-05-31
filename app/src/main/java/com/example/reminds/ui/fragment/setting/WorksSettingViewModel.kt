package com.example.reminds.ui.fragment.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.usecase.db.topic.GetTopicByIdUseCase
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class WorksSettingViewModel @Inject constructor(private val getTopicByIdUseCase: GetTopicByIdUseCase) : BaseViewModel() {
    var optionSelected: Int = TopicGroupEntity.SHOW_ALL_WORKS
    private val _idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    val getTopicByIdLiveData: LiveData<Int> = _idGroup.switchMapLiveData {
        getTopicByIdUseCase.invoke(GetTopicByIdUseCase.Param(_idGroup.value ?: return@switchMapLiveData)).collect {
            it?.let {
                emit(it.optionSelected)
            }
        }
    }

    fun setIdGroup(idGroup: Long) {
        _idGroup.postValue(idGroup)
    }
}