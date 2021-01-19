package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.*
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.citigo.kiotviet.common.utils.extension.getLastOrNull

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertWorkUseCase: InsertWorkUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase,
    private val updateListWorkUseCase: UpdateListWorkUseCase
) : BaseViewModel() {
    private var isReSaveWorks = false
    private var workPosition = -1

    val isShowDone = true

    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    private var listWorkViewModel: ArrayList<WorkDataEntity> = ArrayList()

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
            it.copy()
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

    private fun reSaveListWorkToDb(works: List<WorkDataEntity>, block: (works: List<WorkDataEntity>) -> Unit) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            val list = works.map {
                it.copyAndClearFocus()
            }
            block.invoke(list)
            updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
        }
    }

    private fun addNewContentToListWork(wPosition: Int) {
        if (listWorkViewModel[wPosition].listContent.getLastOrNull() == null ||
            listWorkViewModel[wPosition].listContent.getLastOrNull()?.name?.isNotBlank() != false
        )
            listWorkViewModel[wPosition].listContent.add(
                ContentDataEntity(
                    System.currentTimeMillis(), "",
                    listWorkViewModel[wPosition].id, isFocus = true
                )
            )
    }


    fun updateListWork(works: List<WorkDataEntity>, wPosition: Int) {
        reSaveListWorkToDb(works) {
            reSaveListWorkViewModel(it)
            workPosition = wPosition
        }
    }

    fun handlerCheckedContent(content: ContentDataEntity, workPosition: Int) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[workPosition].listContent.removeAll { it.id == content.id }
            listWorkViewModel[workPosition].listContentDone.add(content)
            val work = listWorkViewModel[workPosition].copyAndClearFocus()
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))

        }

    fun deleteContent(content: ContentDataEntity, wPosition: Int) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[wPosition].listContent.removeAll { it.id == content.id }
            val work = listWorkViewModel[wPosition].copyAndClearFocus()
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
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
                    return@forEachIndexed
                }
            }
            val work = listWorkViewModel[wPosition].copyAndClearFocus()
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
        }


    fun updateAndAddContent(content: ContentDataEntity, wPosition: Int) {
        workPosition = wPosition
        updateContentData(content, wPosition)
    }

    fun insertNewWork(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val workInsert = WorkDataEntity(
                System.currentTimeMillis(),
                name,
                listWorkData.value?.get(0)?.groupId ?: 0,
                arrayListOf(),
                arrayListOf()
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
