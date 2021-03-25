package com.example.reminds.ui.fragment.upcoming

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.TopicGroupEntity.Companion.TYPE_UPCOMING
import com.example.domain.usecase.db.topic.FetchTopicFlowUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.flow.collect

class UpcomingViewModel @ViewModelInject constructor(fetchTopicFlowUseCase: FetchTopicFlowUseCase) : BaseViewModel() {

    private val _getAllTopicUpComing: LiveData<List<TopicGroupEntity>> = liveData {
        fetchTopicFlowUseCase.invoke(FetchTopicFlowUseCase.Param(TYPE_UPCOMING)).collect {
            emit(it)
        }
    }

    val getAllTopicUpComing = _getAllTopicUpComing.switchMapLiveDataEmit {
        it
    }

    var isExpandedCalendar: Boolean = true

}