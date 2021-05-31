package com.example.reminds.ui.fragment.upcoming

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.TopicGroupEntity.Companion.TYPE_UPCOMING
import com.example.domain.usecase.db.topic.FetchTopicFlowUseCase
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(fetchTopicFlowUseCase: FetchTopicFlowUseCase) : BaseViewModel() {
    var isExpandedCalendar: Boolean = false

    private val _getAllTopicUpComing: LiveData<List<TopicGroupEntity>> = liveData {
        fetchTopicFlowUseCase.invoke(FetchTopicFlowUseCase.Param(TYPE_UPCOMING)).collect {
            emit(it)
        }
    }

    val getAllTopicUpComing = _getAllTopicUpComing.switchMapLiveDataEmit {
        it
    }
}