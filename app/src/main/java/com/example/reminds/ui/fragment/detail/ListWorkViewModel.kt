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
    private lateinit var topicGroup: TopicGroupEntity
    private var isReSaveWorks = false
    private var workId: Long = -1
    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()
    var listWorkViewModel: ArrayList<WorkDataEntity> = ArrayList()

    val positionFocused: LiveData<Long> = MutableLiveData()

    private val _isShowDoneLiveData: LiveData<Boolean> = idGroup.switchMapLiveData {
        getTopicByIdUseCase.invoke(GetTopicByIdUseCase.Param(idGroup.value ?: return@switchMapLiveData)).collect {
            topicGroup = it
            emit(topicGroup.isShowDone)
        }
    }

    val isShowDoneLiveData: LiveData<Boolean> = _isShowDoneLiveData.switchMapLiveDataEmit {
        it
    }

    private val listWorkDataLocal: LiveData<List<WorkDataEntity>> = _isShowDoneLiveData.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup.value ?: return@switchMapLiveData))
            .collect {
                emit(it)
            }
    }

    val listWorkData = listWorkDataLocal.switchMapLiveDataEmit { it ->
        reSaveListWorkViewModel(it)
        if (workId != -1L) {
            addNewContentToListWork(workId)
            workId = -1L
        }
        listWorkViewModel.let { it ->
            if (_isShowDoneLiveData.value == true) {
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

    fun saveTopicGroup(isShowDone: Boolean) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            topicGroup.isShowDone = isShowDone
            updateTopicUseCase.invoke(UpdateTopicUseCase.Param(topicGroup))
        }
    }


    fun getListWork(idGroup: Long) {
        this.idGroup.postValue(idGroup)
        isReSaveWorks = true
    }

    private fun reSaveListWorkViewModel(list: List<WorkDataEntity>) {
        listWorkViewModel = arrayListOf()
        listWorkViewModel.addAll(list)
        isReSaveWorks = false
    }

    fun reSaveListWorkToDb(wId: Long) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            val list = listWorkViewModel.map {
                it.copyState()
            }
            workId = wId
            updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
        }
    }

    fun reSaveListWorkAndCreateStateFocus() {
        GlobalScope.launch(handler + Dispatchers.IO) {
            val list = listWorkViewModel.map {
                it.copyAndResetFocus()
            }
            positionFocused.postValue(-1)
            isReSaveWorks = true
            updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
        }
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
                positionFocused.postValue(newContent.id)
                it.listContent.add(newContent)
            }
        }
    }

    fun handlerCheckedContent(content: ContentDataEntity, workId: Long) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            isReSaveWorks = true
            listWorkViewModel.getOrNull {
                this.id == workId
            }?.copyState()?.let { it ->
                if (it.groupId == 1L && content.isCheckDone) {
                    deleteContent(content, workId)
                    return@launch
                }
                it.listContent.getOrNull {
                    this.id == content.id
                }?.apply {
                    isCheckDone = content.isCheckDone
                }
                it.doneAll = it.listContent.all { it.isCheckDone }
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
        updateContentData(content, wId)
    }

    fun updateContentData(content: ContentDataEntity, wId: Long) =
        viewModelScope.launch(handler + Dispatchers.IO) {
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

    fun insertNewWork(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val workInsert = WorkDataEntity(
                id = System.currentTimeMillis(),
                name = name,
                groupId = idGroup.value ?: 0,
                listContent = arrayListOf(), doneAll = false
            )
            listWorkViewModel.add(workInsert)
            insertWorkUseCase.invoke(
                InsertWorkUseCase.Param(
                    workInsert
                )
            )
        }
    }

    fun deleteWork(workId: Long) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            isReSaveWorks = true
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
            isReSaveWorks = true
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
        }
    }
}
