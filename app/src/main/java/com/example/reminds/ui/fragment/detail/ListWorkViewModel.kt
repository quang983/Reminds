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
    private val insertListWorkUseCase: InsertListWorkUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase,
    private val updateListWorkUseCase: UpdateListWorkUseCase,
) : BaseViewModel() {
    private var isReSaveWorks = false

    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    private val listWorkViewModel: ArrayList<WorkDataEntity> = ArrayList()

    private val listWorkDataLocal: LiveData<List<WorkDataEntity>> = idGroup.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup.value ?: return@switchMapLiveData))
            .collect {
                emit(it)
            }
    }

    val listWorkData = listWorkDataLocal.switchMapLiveDataEmit {
        if (isReSaveWorks) {
            reSaveListWork(it)
        }
        /* listWorkViewModel.forEach { it ->
             it.listContent.removeAll { it.isChecked && !it.isFocus }
         }
         val list =listWorkViewModel*/
        val list = listWorkViewModel.map { it ->
            WorkDataEntity(it.id, it.name, it.groupId, it.listContent.map {
                ContentDataEntity(it.id, it.name, it.idOwnerWork, it.isChecked, it.isFocus)
            } as MutableList<ContentDataEntity>)
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

    fun addNewContentToListWork(wPosition: Int) {
        if (listWorkViewModel[wPosition].listContent.getLastOrNull() == null ||
            listWorkViewModel[wPosition].listContent.getLastOrNull()?.name?.isNotBlank() != false
        )
            listWorkViewModel[wPosition].listContent.add(
                ContentDataEntity(
                    System.currentTimeMillis(), "",
                    listWorkViewModel[wPosition].id, isChecked = false, isFocus = true
                )
            )
    }

    fun insertWorksObject(works: List<WorkDataEntity>) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            insertListWorkUseCase.invoke(InsertListWorkUseCase.Param(works))
        }
    }

    fun updateListWork(works: List<WorkDataEntity>, wPosition: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val list = works.map { it ->
                WorkDataEntity(it.id, it.name, it.groupId,
                    it.listContent.filter { it.name.isNotBlank() } as MutableList<ContentDataEntity>)
            }
            reSaveListWork(list)
            addNewContentToListWork(wPosition)
            updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
        }
    }

    fun updateContent(content: ContentDataEntity, contentPosition: Int, workPosition: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[workPosition].listContent[contentPosition] = content

            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(listWorkViewModel[workPosition]))
        }
    }

    fun updateAndAddContent(content: ContentDataEntity, contentPosition: Int, workPosition: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[workPosition].listContent[contentPosition] = content
            val work = listWorkViewModel[workPosition]
            val workUpdate = WorkDataEntity(work.id, work.name, work.groupId,
                work.listContent.filter { it.name.isNotBlank() } as MutableList<ContentDataEntity>)
            addNewContentToListWork(workPosition)
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(workUpdate))
        }
    }

    fun insertWorkInCurrent(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val workInsert = WorkDataEntity(
                System.currentTimeMillis(),
                name, listWorkData.value?.get(0)?.groupId ?: 0, arrayListOf()
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
