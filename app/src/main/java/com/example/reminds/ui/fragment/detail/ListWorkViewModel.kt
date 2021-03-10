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

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertWorkUseCase: InsertWorkUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase,
    private val updateListWorkUseCase: UpdateListWorkUseCase,
    private val getTopicByIdUseCase: GetTopicByIdUseCase,
    private val updateTopicUseCase: UpdateTopicUseCase,
    private val deleteWorkUseCase: DeleteWorkUseCase
) : BaseViewModel() {
    private lateinit var _topicGroup: TopicGroupEntity

    private var _workId: Long = -1
    private val _idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    var listWorkViewModel: ArrayList<WorkDataEntity> = ArrayList()

    private val _isShowDoneLiveData: LiveData<Boolean> = _idGroup.switchMapLiveData {
        getTopicByIdUseCase.invoke(GetTopicByIdUseCase.Param(_idGroup.value ?: return@switchMapLiveData)).collect {
            _topicGroup = it
            emit(_topicGroup.isShowDone)
        }
    }

    val isShowDoneLiveData: LiveData<Boolean> = _isShowDoneLiveData.switchMapLiveDataEmit {
        it
    }

    private val _listWorkDataLocal: LiveData<List<WorkDataEntity>> = _isShowDoneLiveData.switchMapLiveData {
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
            if (_isShowDoneLiveData.value != true) {
                it.filter { !it.doneAll }
            } else {
                it
            }
        }.map {
            it.copy()
        }.sortedWith(
            compareBy({ it.doneAll }, { it.id })
        )
    }

    private fun addNewContentToListWork(workId: Long) {
        listWorkViewModel.getOrNull { id == workId }?.let {
            if (it.listContent.getLastOrNull() == null ||
                it.listContent.getLastOrNull()?.name?.isNotBlank() != false
            ) {
                val newContent = ContentDataEntity(
                    id = System.currentTimeMillis(), name = "",
                    idOwnerWork = it.id,
                    isFocus = true, isCheckDone = false
                )
                it.listContent.add(newContent)
            }
        }
    }

    private fun reSaveListWorkViewModel(list: List<WorkDataEntity>) {
        listWorkViewModel = arrayListOf()
        listWorkViewModel.addAll(list)
    }

    fun saveTopicGroup(isShowDone: Boolean) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            _topicGroup.isShowDone = isShowDone
            updateTopicUseCase.invoke(UpdateTopicUseCase.Param(_topicGroup))
        }
    }

    fun getListWork(idGroup: Long) {
        this._idGroup.postValue(idGroup)
    }

    fun reSaveListWorkToDb(wId: Long) = GlobalScope.launch(handler + Dispatchers.IO) {
        val list = listWorkViewModel.map {
            it.copyState()
        }
        _workId = wId
        updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
    }


    fun reSaveListWorkAndCreateStateFocus() = GlobalScope.launch(handler + Dispatchers.IO) {
        val list = listWorkViewModel.map {
            it.copyAndResetFocus()
        }
        updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
    }

    fun handlerCheckedContent(content: ContentDataEntity, workId: Long) = viewModelScope.launch(handler + Dispatchers.IO) {
        listWorkViewModel.getOrNull {
            this.id == workId
        }?.copyState()?.let { it ->
            it.listContent.getOrNull {
                this.id == content.id
            }?.apply {
                isCheckDone = content.isCheckDone
            }
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
        }
    }

    fun deleteContent(content: ContentDataEntity, workId: Long) = viewModelScope.launch(handler + Dispatchers.IO) {
        listWorkViewModel.getOrNull { this.id == workId }?.copyState()?.let { it ->
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
                    contentDataEntity.isFocus = content.isFocus
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

    fun insertNewWork(name: String) = viewModelScope.launch(handler + Dispatchers.IO) {
        val workInsert = WorkDataEntity(
            id = System.currentTimeMillis(),
            name = name,
            groupId = _idGroup.value ?: 0,
            listContent = arrayListOf(), doneAll = false
        )
        listWorkViewModel.add(workInsert)
        insertWorkUseCase.invoke(
            InsertWorkUseCase.Param(
                workInsert
            )
        )
    }

    fun deleteWork(workId: Long) = viewModelScope.launch(handler + Dispatchers.IO) {
        deleteWorkUseCase.invoke(DeleteWorkUseCase.Param(listWorkViewModel.filter { it.id == workId }.getFirstOrNull()))
    }

    fun handleDoneAllContentFromWork(idWork: Long, doneAll: Boolean) = viewModelScope.launch(handler + Dispatchers.IO) {
        listWorkViewModel.getOrNull {
            this.id == idWork
        }?.copyState()?.apply {
            this.doneAll = doneAll
            this.listContent.forEach {
                it.isCheckDone = doneAll
            }
        }?.let { it ->
            if (it.groupId != 1L) {
                updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
            } else {
                deleteWork(it.id)
            }
        }
    }
}
