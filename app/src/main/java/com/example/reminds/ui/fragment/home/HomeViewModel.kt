package com.example.reminds.ui.fragment.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.domain.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.usecase.db.topic.FetchTopicUseCase
import com.example.reminds.common.BaseViewModel

class HomeViewModel @ViewModelInject constructor(fetchTopicUseCase: FetchTopicUseCase) : BaseViewModel() {
    val topicData : LiveData<List<TopicGroupEntity>> = liveDataEmit {
        kotlin.runCatching { fetchTopicUseCase.invoke(BaseUseCase.Param()) }.getOrNull() ?: listOf()
    }
}