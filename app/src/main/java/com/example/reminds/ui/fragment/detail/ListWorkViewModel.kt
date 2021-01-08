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
    private var isWorkChanged: Boolean = false
    private var idWorkFocus: Long = 0
    private var mWorkPosition: Int = -1

    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    private val listWorkDataLocal: LiveData<List<WorkDataEntity>> = idGroup.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup.value ?: return@switchMapLiveData))
            .collect {
                emit(it)
            }
    }

    val listWorkData = listWorkDataLocal.switchMapLiveDataEmit {
        if (mWorkPosition != -1 && isWorkChanged) {
            it[mWorkPosition].listContent.add(
                ContentDataEntity(
                    System.currentTimeMillis(), "",
                    it[mWorkPosition].id, isChecked = false, isFocus = true
                )
            )
            setDefaultValue()
        }
        it
    }

    fun getListWork(idGroup: Long) {
        this.idGroup.postValue(idGroup)
    }

    private fun setDefaultValue() {
        mWorkPosition = -1
        isWorkChanged = false
        idWorkFocus = 0
    }

    fun insertWorksObject(works: List<WorkDataEntity>) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            insertListWorkUseCase.invoke(InsertListWorkUseCase.Param(works))
        }
    }

    fun updateListWork(works: List<WorkDataEntity>, workPosition: Int) {
        if (!works[workPosition].listContent.isNullOrEmpty()) {
            viewModelScope.launch(handler + Dispatchers.IO) {
                mWorkPosition = workPosition
                isWorkChanged = true
                val list = works.map { it ->
                    WorkDataEntity(it.id, it.name, it.groupId,
                        it.listContent.filter { it.name.isNotBlank() } as ArrayList<ContentDataEntity>)
                }
                updateListWorkUseCase.invoke(UpdateListWorkUseCase.Param(list))
            }
        } else {
            insertNewContentToWork(workPosition)
        }
    }

    fun insertNewContentToWork(workPosition: Int) {
        mWorkPosition = workPosition
        isWorkChanged = true
        idGroup.postValue(idGroup.value)
    }

    fun insertWorkInCurrent(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            insertWorkUseCase.invoke(
                InsertWorkUseCase.Param(
                    WorkDataEntity(
                        System.currentTimeMillis(),
                        name, listWorkData.value?.get(0)?.groupId ?: 0, arrayListOf()
                    )
                )
            )
        }
    }

    fun updateWork(work: WorkDataEntity, workPosition: Int, type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (type != TYPE_CHECK_ITEM) {
                mWorkPosition = workPosition
                isWorkChanged = true
            }
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
        }
    }

    companion object {
        const val TYPE_CHECK_ITEM = 0
        const val TYPE_OTHER = 1
    }
}
