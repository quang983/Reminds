package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.topic.GetTopicByIdUseCase
import com.example.domain.usecase.db.topic.UpdateTopicUseCase
import com.example.domain.usecase.db.workintopic.*
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.getFirstOrNull
import com.example.reminds.utils.getLastOrNull
import com.example.reminds.utils.getOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertWorkUseCase: InsertWorkUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase,
    private val updateListWorkUseCase: UpdateListWorkUseCase,
    private val getTopicByIdUseCase: GetTopicByIdUseCase,
    private val updateTopicUseCase: UpdateTopicUseCase,
    private val deleteWorkUseCase: DeleteWorkUseCase
) : BaseViewModel() {
    private var _topicGroup: TopicGroupEntity? = null

    private var _workId: Long = -1

    private val _idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()
    var listWorkViewModel: ArrayList<WorkDataEntity> = ArrayList()

    private val _optionSelectedLiveData: LiveData<Int> = _idGroup.switchMapLiveData {
        getTopicByIdUseCase.invoke(GetTopicByIdUseCase.Param(_idGroup.value ?: return@switchMapLiveData)).collect {
            _topicGroup = it
            it?.let {
                emit(it.optionSelected)
            } ?: _listWorkDataLocal.postValue(emptyList())
        }
    }

    private val _listWorkDataLocal: LiveData<List<WorkDataEntity>> = _optionSelectedLiveData.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(_idGroup.value ?: return@switchMapLiveData))
            .collect {
                emit(it)
            }
    }

    val listWorkData = _listWorkDataLocal.switchMapLiveDataEmit { it ->
        reSaveListWorkViewModel(it)
        if (_workId != -1L) {
            addNewContentToListWork(_workId)
            _workId = -1L
        }
        listWorkViewModel.let { it ->
            if (_optionSelectedLiveData.value == TopicGroupEntity.HIDE_DONE_WORKS) {
                it.filter { !it.doneAll }
            } else {
                it
            }
        }.map {
            it.copySort()
        }.sortedWith(
            compareBy({ it.doneAll }, { it.stt })
        )
    }

    val listWorkViewItems = listWorkData.switchMapLiveDataEmit {
        it
    }

    private fun addNewContentToListWork(workId: Long) {
        listWorkViewModel.getOrNull { id == workId }?.let {
            if (it.listContent.getLastOrNull() == null ||
                it.listContent.getLastOrNull()?.name?.isNotBlank() != false
            ) {
                val newContent = ContentDataEntity(
                    id = System.currentTimeMillis(), name = "",
                    idOwnerWork = it.id, isCheckDone = false
                )
                it.listContent.add(newContent)
            }
        }
    }

    private fun reSaveListWorkViewModel(list: List<WorkDataEntity>) {
        listWorkViewModel = arrayListOf()
        listWorkViewModel.addAll(list)
    }

    fun saveTopicGroup(optionSelected: Int) = viewModelScope.launch(Dispatchers.IO + handler) {
        _topicGroup?.let {
            it.optionSelected = optionSelected
            if (listOf(TopicGroupEntity.SHOW_ALL_WORKS, TopicGroupEntity.HIDE_DONE_WORKS, TopicGroupEntity.REMOVE_DONE_WORKS).contains(optionSelected)) {
                updateTopicUseCase.invoke(UpdateTopicUseCase.Param(it))
            }
        }
    }

    fun getListWork(idGroup: Long) {
        _idGroup.postValue(idGroup)
    }

    fun reSaveListWorkToDb(wId: Long) = GlobalScope.launch(handler + Dispatchers.IO) {
        val list = listWorkViewModel.map {
            it.copyFilterNotEmpty()
        }.apply {
            this.getLastOrNull()?.isShowContents = true
        }
        _workId = wId
        updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
    }


    fun reSaveListWorkAndCreateStateFocus() = GlobalScope.launch(handler + Dispatchers.IO) {
        val list = listWorkViewModel.map {
            it.copyFilterNotEmpty()
        }
        updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
    }

    fun handlerCheckedContent(content: ContentDataEntity, workId: Long) = viewModelScope.launch(handler + Dispatchers.IO) {
        listWorkViewModel.getOrNull {
            this.id == workId
        }?.copyFilterNotEmpty()?.let { it ->
            it.listContent.getOrNull {
                this.id == content.id
            }?.apply {
                isCheckDone = content.isCheckDone
            }
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
        }
    }

    fun deleteContent(content: ContentDataEntity, workId: Long) = viewModelScope.launch(handler + Dispatchers.IO) {
        listWorkViewModel.getOrNull { this.id == workId }?.copyFilterNotEmpty()?.let { it ->
            it.listContent.removeAll { it.id == content.id }.apply {
                updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
            }
        }
    }

    fun updateAndAddContent(content: ContentDataEntity, wId: Long) {
        _workId = wId
        updateContentData(content, wId)
    }

    fun updateContentData(content: ContentDataEntity, wId: Long) = viewModelScope.launch(handler + Dispatchers.IO) {
        listWorkViewModel.getOrNull { id == wId }?.copy()?.let {
            it.listContent.forEachIndexed { _, contentDataEntity ->
                if (content.id == contentDataEntity.id) {
                    contentDataEntity.idOwnerWork = content.idOwnerWork
                    contentDataEntity.name = content.name
                    contentDataEntity.hashTag = content.hashTag
                    contentDataEntity.timer = content.timer
                    contentDataEntity.isCheckDone = content.isCheckDone
                    return@forEachIndexed
                }
            }
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
        }
    }

/*    fun insertNewWork(name: String, typeTopic: Int) = viewModelScope.launch(handler + Dispatchers.IO) {
        val workInsert = WorkDataEntity(
            id = System.currentTimeMillis(),
            name = name,
            groupId = _idGroup.value ?: 0,
            listContent = arrayListOf(), doneAll = false
        )
        listWorkViewModel.add(workInsert)
        insertWorkUseCase.invoke(
            InsertWorkUseCase.Param(
                workInsert, typeTopic
            )
        )
    }*/

    fun deleteWork(workId: Long) = viewModelScope.launch(handler + Dispatchers.IO) {
        deleteWorkUseCase.invoke(DeleteWorkUseCase.Param(listWorkViewModel.filter { it.id == workId }.getFirstOrNull()))
    }

    fun handleDoneAllContentFromWork(idWork: Long, doneAll: Boolean) = viewModelScope.launch(handler + Dispatchers.IO) {
        listWorkViewModel.getOrNull {
            this.id == idWork
        }?.copyFilterNotEmpty()?.apply {
            this.doneAll = doneAll
            this.listContent.forEach {
                it.isCheckDone = doneAll
            }
        }?.let { it ->
            if (it.groupId != 1L && _optionSelectedLiveData.value != TopicGroupEntity.REMOVE_DONE_WORKS) {
                updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
            } else {
                deleteWork(it.id)
            }
        }
    }

    fun updateWorkChange(work: WorkDataEntity, addNewContent: Boolean) = viewModelScope.launch(Dispatchers.IO + handler) {
        if (addNewContent) {
            _workId = work.id
        }
        updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
    }

    fun saveListWork(currentList: List<WorkDataEntity>) = viewModelScope.launch(Dispatchers.IO + handler) {
        val listIndex = mutableListOf<WorkDataEntity>()
        currentList.forEachIndexed { index, workDataEntity ->
            workDataEntity.stt = index
            listIndex.add(workDataEntity)
        }
        updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(listIndex))
    }
}
