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
    private val updateListWorkUseCase: UpdateListWorkUseCase
) : BaseViewModel() {
    private var isReSaveWorks = false
    private var workPosition = -1

    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

//    private var listWorkViewModel= listWorkData.sw

    private val fetchListWorkDataLocal: LiveData<List<WorkDataEntity>> = idGroup.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup.value ?: return@switchMapLiveData))
            .collect {
                emit(it)
            }
    }

    val listWorkData = fetchListWorkDataLocal.switchMapLiveDataEmit { it ->
        if (workPosition != -1) {
            if (it[workPosition].listContent.getLastOrNull() == null ||
                it[workPosition].listContent.getLastOrNull()?.name?.isNotBlank() != false
            )
                it[workPosition].listContent.add(
                    ContentDataEntity(
                        System.currentTimeMillis(), "",
                        it[workPosition].id, isFocus = true
                    )
                )
            workPosition = -1
        }
        val list = it.map {
            it.copy()
        }
        listWorkViewModel.clear()
        listWorkViewModel.addAll(list)
        list
    }

    private var listWorkViewModel = mutableListOf<WorkDataEntity>()


    fun getListWork(idGroup: Long) {
        this.idGroup.postValue(idGroup)
        isReSaveWorks = true
    }

    fun updateListWork(works: List<WorkDataEntity>, wPosition: Int) {
        reSaveListWorkToDb(works) {
            workPosition = wPosition
        }
    }

    fun reSaveListWorkToDb(works: List<WorkDataEntity>, block: (works: List<WorkDataEntity>) -> Unit) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            val list = works.map {
                it.copy()
            }
            block.invoke(list)
            updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
        }
    }

    fun handlerCheckedContent(content: ContentDataEntity, workPosition: Int) =
        viewModelScope.launch(handler + Dispatchers.IO) {
            listWorkViewModel[workPosition].listContent.removeAll { it.id == content.id }
            listWorkViewModel[workPosition].listContentDone.add(content)
            val work = listWorkViewModel[workPosition].copyAndClearFocus()
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))

        }

    fun updateNameContent(content: ContentDataEntity, workPosition: Int) {
        listWorkViewModel[workPosition].listContent.forEachIndexed { _, contentDataEntity ->
            if (content.id == contentDataEntity.id) {
                contentDataEntity.name = content.name
                return@forEachIndexed
            }
        }
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
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(listWorkViewModel[wPosition]))
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
