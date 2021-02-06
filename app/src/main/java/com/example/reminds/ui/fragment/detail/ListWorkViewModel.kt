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
    private val updateTopicUseCase: UpdateTopicUseCase
) : BaseViewModel() {
    private lateinit var topicGroup: TopicGroupEntity

    private var isReSaveWorks = false
    private var workPosition = -1


    var workPositionSelected = 0

    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    var listWorkViewModel: ArrayList<WorkDataEntity> = ArrayList()

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
        if (isReSaveWorks) {
            reSaveListWorkViewModel(it)
        }
        if (workPosition != -1) {
            addNewContentToListWork(workPosition)
            workPosition = -1
        }
        val list = listWorkViewModel.map {
            if (_isShowDoneLiveData.value == true) {
                it.copy()
            } else {
                it.copyAndRemoveDone()
            }
        }
        list
    }

    fun saveTopicGroup(isShowDone: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            topicGroup.isShowDone = isShowDone
            updateTopicUseCase.invoke(UpdateTopicUseCase.Param(topicGroup))
        }
    }


    fun getListWork(idGroup: Long) {
        this.idGroup.postValue(idGroup)
        isReSaveWorks = true
    }

    private fun reSaveListWorkViewModel(list: List<WorkDataEntity>) {
        listWorkViewModel.clear()
        listWorkViewModel.addAll(list)
        isReSaveWorks = false
    }

    fun reSaveListWorkToDb(wPosition: Int) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            val list = listWorkViewModel.map {
                it.copyState()
            }
            workPosition = wPosition
            updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
        }
    }

    fun reSaveListWorkAndCreateStateFocus() {
        GlobalScope.launch(handler + Dispatchers.IO) {
            val list = listWorkViewModel.map {
                it.copyAndResetFocus()
            }
            isReSaveWorks = true
            updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
        }
    }


    private fun addNewContentToListWork(wPosition: Int) {
        if (listWorkViewModel[wPosition].listContent.getLastOrNull() == null ||
            listWorkViewModel[wPosition].listContent.getLastOrNull()?.name?.isNotBlank() != false
        )
            listWorkViewModel[wPosition].listContent.add(
                ContentDataEntity(
                    id = System.currentTimeMillis(), name = "",
                    idOwnerWork = listWorkViewModel[wPosition].id,
                    isFocus = true, isCheckDone = false
                )
            )
    }

    fun handlerCheckedContent(content: ContentDataEntity, workPosition: Int) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            if (listWorkViewModel[workPosition].groupId == 1L && content.isCheckDone) {
                deleteContent(content, workPosition)
                return@launch
            }
            listWorkViewModel[workPosition].listContent.getOrNull {
                this.id == content.id
            }?.apply {
                isCheckDone = content.isCheckDone
            }.let {
                val work = listWorkViewModel[workPosition].copyState()
                updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
            }

        }

    fun deleteContent(content: ContentDataEntity, wPosition: Int) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[wPosition].listContent.removeAll { it.id == content.id }
            val work = listWorkViewModel[wPosition].copyState()
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
        }

    fun updateAndAddContent(content: ContentDataEntity, wPosition: Int) {
        workPosition = wPosition
        updateContentData(content, wPosition)
    }

    fun updateContentData(content: ContentDataEntity, wPosition: Int) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[wPosition].listContent.forEachIndexed { _, contentDataEntity ->
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
            val work = listWorkViewModel[wPosition].copyState()
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
        }

    fun insertNewWork(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val workInsert = WorkDataEntity(
                id = System.currentTimeMillis(),
                name = name,
                groupId = listWorkData.value?.get(0)?.groupId ?: 0,
                listContent = arrayListOf()
            )
            listWorkViewModel.add(workInsert)
            insertWorkUseCase.invoke(
                InsertWorkUseCase.Param(
                    workInsert
                )
            )
        }
    }
}
