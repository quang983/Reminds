package com.example.reminds.ui.fragment.newtopic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewTopicBtsViewModel @ViewModelInject constructor(private val insertTopicUseCase: InsertTopicUseCase) :
    BaseViewModel() {
    val timeStamps = 0L
    fun insertTopic(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val data = TopicGroupEntity(System.currentTimeMillis(), name, timeStamps)
            insertTopicUseCase.invoke(InsertTopicUseCase.Param(data))
        }
    }
}
