package com.example.reminds.ui.fragment.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.usecase.db.topic.DeleteTopicUseCase
import com.example.domain.usecase.db.topic.FetchTopicFlowUseCase
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.domain.usecase.db.workintopic.GetTotalTaskOfWorkUseCase
import com.example.domain.usecase.db.workintopic.InsertWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val fetchTopicFlowUseCase: FetchTopicFlowUseCase,
    private val deleteTopicUseCase: DeleteTopicUseCase,
    private val getTotalTaskOfWorkUseCase: GetTotalTaskOfWorkUseCase,
    private val insertTopicUseCase: InsertTopicUseCase,
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertWorkUseCase: InsertWorkUseCase
) : BaseViewModel() {

    private val _addFirstTopicDoneLiveData : LiveData<Boolean> = MutableLiveData()

    val fastTopicData: LiveData<List<WorkDataEntity>> = _addFirstTopicDoneLiveData.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(1))
            .collect {
                emit(it)
            }
    }

    private val _topicData: LiveData<List<TopicGroupEntity>> = liveData {
        fetchTopicFlowUseCase.invoke(BaseUseCase.Param()).collect {
            emit(it.toMutableList())
        }
    }

    val topicsGroupDataShow: LiveData<List<TopicGroupViewItem>> = _topicData.switchMapLiveDataEmit { it ->
        it.map {
            TopicGroupViewItem(it, getTotalTaskOfWorkUseCase.invoke(GetTotalTaskOfWorkUseCase.Param(it.id)))
        }
    }

    fun deleteTopicData(item: TopicGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTopicUseCase.invoke(DeleteTopicUseCase.Param(listOf(item)))
        }
    }


    fun addFirstTopic(topic: String) = viewModelScope.launch(handler + Dispatchers.IO) {
        val data = TopicGroupEntity(1, "Today", false, TopicGroupEntity.REMOVE_DONE_WORKS)
        insertTopicUseCase.invoke(InsertTopicUseCase.Param(data)).let {
            insertWorkUseCase.invoke(
                InsertWorkUseCase.Param(
                    WorkDataEntity(
                        id = System.currentTimeMillis(),
                        name = topic,
                        groupId = it,
                        listContent = mutableListOf(),
                        doneAll = false,
                        isShowContents = false
                    )
                )
            )
        }
        _addFirstTopicDoneLiveData.postValue(true)
    }

    fun insertTopic(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val data = TopicGroupEntity(System.currentTimeMillis(), name)
            kotlin.runCatching {
                insertTopicUseCase.invoke(InsertTopicUseCase.Param(data))
            }
        }
    }

    data class TopicGroupViewItem(val topicGroupEntity: TopicGroupEntity, val totalTask: Int)

    data class FastTopicViewItem(val topicGroupEntity: TopicGroupEntity, val contents: List<ContentDataEntity>)
}