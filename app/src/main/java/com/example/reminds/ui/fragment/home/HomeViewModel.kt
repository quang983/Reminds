package com.example.reminds.ui.fragment.home

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.base.BaseUseCase
import com.example.domain.model.TopicGroupEntity
import com.example.domain.usecase.db.topic.DeleteTopicUseCase
import com.example.domain.usecase.db.topic.FetchTopicUseCase
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(
    private val fetchTopicUseCase: FetchTopicUseCase,
    private val deleteTopicUseCase: DeleteTopicUseCase,
    private val insertTopicUseCase: InsertTopicUseCase
) :
    BaseViewModel() {
    val topicData: LiveData<List<TopicGroupEntity>> = liveData {
        fetchTopicUseCase.invoke(BaseUseCase.Param()).collect {
            Log.d("quangtd", "observeData: ${it.size}")
            emit(it)
        }
    }

    fun deleteTopicData(item: TopicGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTopicUseCase.invoke(DeleteTopicUseCase.Param(listOf(item)))
        }
    }

    fun undoTopicData(item: TopicGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            insertTopicUseCase.invoke(InsertTopicUseCase.Param(listOf(item)))
        }
    }
}