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

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertWorkUseCase: InsertWorkUseCase,
    private val insertListWorkUseCase: InsertListWorkUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase,
    private val updateListWorkUseCase: UpdateListWorkUseCase,
) : BaseViewModel() {
    private var isReSaveWorks = false
    private var workPosition = -1

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
            reSaveListWork(it)
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

    private fun reSaveListWork(list: List<WorkDataEntity>) {
        listWorkViewModel.clear()
        listWorkViewModel.addAll(list)
        isReSaveWorks = false
    }

    private fun addNewContentToListWork(wPosition: Int) {
        listWorkViewModel[wPosition].listContent.removeAll { it.id == 0L }
        listWorkViewModel[wPosition].listContent.add(
            ContentDataEntity(
                0, "",
                listWorkViewModel[wPosition].id, isFocus = true
            )
        )
    }

    fun insertWorksObject(works: List<WorkDataEntity>) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            insertListWorkUseCase.invoke(InsertListWorkUseCase.Param(works))
        }
    }

    fun updateListWork(works: List<WorkDataEntity>, wPosition: Int) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            val list = works.map {
                it.copyAndClearFocus()
            }
            reSaveListWork(list)
            workPosition = wPosition
            updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
        }

    fun handlerCheckItem(content: ContentDataEntity, workPosition: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[workPosition].listContent.removeAll { it.id == content.id }
            listWorkViewModel[workPosition].listContentDone.add(content)
            val work = listWorkViewModel[workPosition].copyAndClearFocus()
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
        }
    }

    fun updateAndAddContent(content: ContentDataEntity, contentPosition: Int, wPosition: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            workPosition = wPosition
            listWorkViewModel[workPosition].listContent[contentPosition] = content
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(listWorkViewModel[workPosition]))
        }
    }

    fun insertWorkInCurrent(name: String) {
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
