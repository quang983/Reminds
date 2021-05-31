package com.example.reminds.ui.sharedviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.usecase.db.topic.FindTopicByIdUseCase
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val findTopicByIdUseCase: FindTopicByIdUseCase
) : BaseViewModel() {
    var selectedDate: LocalDate = LocalDate.now()

    val today: LocalDate = LocalDate.now()

    val isKeyboardShow: LiveData<Boolean> = MutableLiveData(false)

    val showAdsMobile: LiveData<Boolean> = MutableLiveData(false)

    val notifyDataInsert: LiveData<AlarmNotificationEntity> = MutableLiveData()

    val navigateToFragmentFromIntent: LiveData<TopicGroupEntity> = MutableLiveData()

    fun setNotifyDataInsert(alarm: AlarmNotificationEntity) = GlobalScope.launch(Dispatchers.IO + handler) {
        notifyDataInsert.postValue(alarm)
    }

    fun getTopic(idTopic: Long) = viewModelScope.launch(Dispatchers.IO + handler) {
        findTopicByIdUseCase.invoke(FindTopicByIdUseCase.Param(idTopic))?.let {
            navigateToFragmentFromIntent.postValue(it)
        }
    }
}