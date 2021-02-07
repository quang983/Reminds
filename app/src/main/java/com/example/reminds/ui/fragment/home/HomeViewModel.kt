package com.example.reminds.ui.fragment.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.base.BaseUseCase
import com.example.domain.usecase.db.content.GetAllContentOfTopicUseCase
import com.example.domain.usecase.db.topic.DeleteTopicUseCase
import com.example.domain.usecase.db.topic.FetchTopicFlowUseCase
import com.example.domain.usecase.db.topic.GetFastTopicUseCase
import com.example.domain.usecase.db.topic.InsertTopicUseCase
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
    private val getFastTopicUseCase: GetFastTopicUseCase,
    private val getAllContentOfTopicUseCase: GetAllContentOfTopicUseCase,
    private val insertTopicUseCase: InsertTopicUseCase,
    private val insertWorkUseCase: InsertWorkUseCase
) : BaseViewModel() {

    val fastTopicData: LiveData<FastTopicViewItem> = MutableLiveData()

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

    fun getFastTopic() {
        viewModelScope.launch(Dispatchers.IO) {
            getFastTopicUseCase.invoke(BaseUseCase.Param()).collect {
                val contents = getAllContentOfTopicUseCase.invoke(GetAllContentOfTopicUseCase.Param(it.id))
                fastTopicData.postValue(FastTopicViewItem(it, contents))
            }
        }
    }

    fun insertTopic(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val data = TopicGroupEntity(System.currentTimeMillis(), name)
            kotlin.runCatching {
                val idTopic = insertTopicUseCase.invoke(InsertTopicUseCase.Param(data))
                insertWorkUseCase.invoke(
                    InsertWorkUseCase.Param(
                        WorkDataEntity(
                            id = System.currentTimeMillis(),
                            name = "Chung",
                            groupId = idTopic,
                            listContent = mutableListOf()
                        )
                    )
                )
            }
        }
    }


    data class TopicGroupViewItem(val topicGroupEntity: TopicGroupEntity, val totalTask: Int)

    data class FastTopicViewItem(val topicGroupEntity: TopicGroupEntity, val contents: List<ContentDataEntity>)
}