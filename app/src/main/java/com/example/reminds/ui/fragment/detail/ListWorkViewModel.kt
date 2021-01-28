package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
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
    private val updateListWorkUseCase: UpdateListWorkUseCase
) : BaseViewModel() {
    private var isReSaveWorks = false
    private var workPosition = -1

    private val isShowDone = true

    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    var listWorkViewModel: ArrayList<WorkDataEntity> = ArrayList()

    private val listWorkDataLocal: LiveData<List<WorkDataEntity>> = idGroup.switchMapLiveData {
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
            if (isShowDone) {
                it.copy()
            } else {
                it.copyAndRemoveDone()
            }
        }
        list
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
                it.copyAndClearFocus()
            }
            workPosition = wPosition
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
                val work = listWorkViewModel[workPosition].copyAndClearFocus()
                updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
            }

        }

    fun deleteContent(content: ContentDataEntity, wPosition: Int) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[wPosition].listContent.removeAll { it.id == content.id }
            val work = listWorkViewModel[wPosition].copyAndClearFocus()
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
            val work = listWorkViewModel[wPosition].copyAndClearFocus()
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
