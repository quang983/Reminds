package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.*
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.getOrEmpty
import com.example.reminds.utils.getOrNull
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

    private val currentWork: MediatorLiveData<WorkDataEntity?> = MediatorLiveData<WorkDataEntity?>()

    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    private val listWorkDataLocal: LiveData<List<WorkDataEntity>> = idGroup.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup.value ?: return@switchMapLiveData)).collect {
            emit(it)
        }
    }


    val listWorkData = MediatorLiveData<Any>().addSources(listWorkDataLocal, currentWork).switchMapLiveDataEmit {
        listWorkDataLocal.getOrEmpty().mapIndexed { index, it ->
            val data = it.clone()

            val listItem = data.listContent.filter { it.name.isNotEmpty() }.toMutableList()

            if (currentWork.getOrNull() == data) {
                listItem.add(ContentDataEntity(-1L, "", idOwnerWork = data.id, isChecked = false, isFocus = true))
            }

            data.listContent = listItem

            data
        }
    }

    fun getListWork(idGroup: Long) {
        this.idGroup.postValue(idGroup)
    }

    fun insertWorksObject(works: List<WorkDataEntity>) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            insertListWorkUseCase.invoke(InsertListWorkUseCase.Param(works))
        }
    }

    fun updateListWork(works: List<WorkDataEntity>, workPosition: Int) = viewModelScope.launch(handler + Dispatchers.IO) {
        currentWork.postValue(listWorkDataLocal.getOrEmpty()[workPosition])
    }

    fun updateContent(content: ContentDataEntity, contentPosition: Int, workPosition: Int) = updateContent(contentPosition, workPosition) {
        isChecked = true
    }

    fun updateAndAddContent(content: ContentDataEntity, contentPosition: Int, workPosition: Int) = updateContent(contentPosition, workPosition) {
        name = content.name
    }

    private fun updateContent(contentPosition: Int, workPosition: Int, update: ContentDataEntity.() -> Unit) = viewModelScope.launch(handler + Dispatchers.IO) {
        listWorkDataLocal.getOrEmpty().getOrNull(workPosition)?.apply {
            val clone = clone()

            val item = clone.listContent.getOrNull(contentPosition)?.apply {
                id = id.takeIf { it >= 0 } ?: System.currentTimeMillis()
            } ?: ContentDataEntity(System.currentTimeMillis(), "", idOwnerWork = id)

            update(item)

            clone.listContent = clone.listContent.toMutableList().apply {
                val index = indexOf(item)
                if (index in 0 until size) set(index, item)
                else add(
                    item
                )
            }

            clone.apply {
                listContent = listContent.filter { it.name.isNotEmpty() }
            }

            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(clone))

            return@launch
        }
    }


    fun insertWorkInCurrent(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
//            val workInsert = WorkDataEntity(
//                System.currentTimeMillis(),
//                name, listWorkData.value?.get(0)?.groupId ?: 0, arrayListOf()
//            )
//            listWorkViewModel.add(workInsert)
//            insertWorkUseCase.invoke(
//                InsertWorkUseCase.Param(
//                    workInsert
//                )
//            )
        }
    }
}
