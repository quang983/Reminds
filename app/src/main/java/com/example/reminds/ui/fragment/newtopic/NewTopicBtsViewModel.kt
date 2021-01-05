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
    fun insertTopic(data: TopicGroupEntity) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            insertTopicUseCase.invoke(InsertTopicUseCase.Param(data))
        }
    }
}
