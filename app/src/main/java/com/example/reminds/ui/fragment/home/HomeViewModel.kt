package com.example.reminds.ui.fragment.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.usecase.db.topic.DeleteTopicUseCase
import com.example.domain.usecase.db.topic.FetchTopicUseCase
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.domain.usecase.db.workintopic.GetTotalTaskOfWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val fetchTopicUseCase: FetchTopicUseCase,
    private val deleteTopicUseCase: DeleteTopicUseCase,
    private val insertTopicUseCase: InsertTopicUseCase,
    private val getTotalTaskOfWorkUseCase: GetTotalTaskOfWorkUseCase
) : BaseViewModel() {
    var reUpdateTopic: LiveData<Boolean> = MutableLiveData()

    private val _topicData: LiveData<List<TopicGroupEntity>> = reUpdateTopic.switchMapLiveData {
        fetchTopicUseCase.invoke(BaseUseCase.Param()).collect { it ->
            val list = it.filter { it.startDate != 0L }.sortedBy { it.startDate }.toMutableList()
            list.addAll(it.filter { it.startDate == 0L })
            emit(list)
        }
    }

    val topicsGroupDataShow: LiveData<List<TopicGroupViewItem>> = _topicData.switchMapLiveDataEmit { it ->
        it.map {
            TopicGroupViewItem(it, getTotalTaskOfWorkUseCase.invoke(GetTotalTaskOfWorkUseCase.Param(it.id)))
        }
    }

    fun reUpdateTopics(isUpdate: Boolean) = reUpdateTopic.postValue(isUpdate)

    fun deleteTopicData(item: TopicGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTopicUseCase.invoke(DeleteTopicUseCase.Param(listOf(item)))
        }
    }

    fun undoTopicData(item: TopicGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            insertTopicUseCase.invoke(InsertTopicUseCase.Param(item))
        }
    }

    data class TopicGroupViewItem(val topicGroupEntity: TopicGroupEntity, val totalTask: Int)
}