package com.example.reminds.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.TopicGroupEntity.Companion.TYPE_FAST
import com.example.common.base.model.TopicGroupEntity.Companion.TYPE_NORMAL
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.topic.DeleteTopicUseCase
import com.example.domain.usecase.db.topic.FetchTopicFlowUseCase
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.domain.usecase.db.workintopic.GetTotalTaskOfWorkUseCase
import com.example.domain.usecase.db.workintopic.InsertListWorkUseCase
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchTopicFlowUseCase: FetchTopicFlowUseCase,
    private val deleteTopicUseCase: DeleteTopicUseCase,
    private val getTotalTaskOfWorkUseCase: GetTotalTaskOfWorkUseCase,
    private val insertTopicUseCase: InsertTopicUseCase,
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertWorksUseCase: InsertListWorkUseCase
) : BaseViewModel() {

    private val _addFirstTopicDoneLiveData: LiveData<Boolean> = MutableLiveData()

    val fastTopicData: LiveData<List<WorkDataEntity>> = _addFirstTopicDoneLiveData.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(1))
            .collect {
                emit(it)
            }
    }

    private val _topicData: LiveData<List<TopicGroupEntity>> = liveData {
        fetchTopicFlowUseCase.invoke(FetchTopicFlowUseCase.Param(TYPE_NORMAL)).collect {
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


    fun addFirstTopic(name: String, name1: String, name2: String) = viewModelScope.launch(handler + Dispatchers.IO) {
        val dataTopic = TopicGroupEntity(1, "Home", false, TopicGroupEntity.REMOVE_DONE_WORKS)
        val idTopic = insertTopicUseCase.invoke(InsertTopicUseCase.Param(dataTopic))
        val data = WorkDataEntity(
            id = System.currentTimeMillis(),
            name = name,
            groupId = idTopic,
            listContent = mutableListOf(),
            doneAll = false,
            isShowContents = false
        )
        val data1 = WorkDataEntity(
            id = System.currentTimeMillis() + 1,
            name = name1,
            groupId = idTopic,
            listContent = mutableListOf(),
            doneAll = false,
            isShowContents = false
        )

        val data2 = WorkDataEntity(
            id = System.currentTimeMillis() + 2,
            name = name2,
            groupId = idTopic,
            listContent = mutableListOf(),
            doneAll = false,
            isShowContents = false
        )


        val list = listOf(data, data1, data2)
        insertWorksUseCase.invoke(InsertListWorkUseCase.Param(list, TYPE_FAST))
        _addFirstTopicDoneLiveData.postValue(true)
    }

    fun postAddFirstTopic(isAdded: Boolean) = _addFirstTopicDoneLiveData.postValue(isAdded)

    data class TopicGroupViewItem(val topicGroupEntity: TopicGroupEntity, val totalTask: Int)
}