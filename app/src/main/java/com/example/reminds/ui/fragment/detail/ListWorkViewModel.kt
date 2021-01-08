package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.content.InsertContentUseCase
import com.example.domain.usecase.db.content.UpdateContentsUseCase
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.domain.usecase.db.workintopic.InsertListWorkUseCase
import com.example.domain.usecase.db.workintopic.InsertWorkUseCase
import com.example.domain.usecase.db.workintopic.UpdateWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertContentUseCase: InsertContentUseCase,
    private val insertWorkUseCase: InsertWorkUseCase,
    private val insertListWorkUseCase: InsertListWorkUseCase,
    private val updateContentsUseCase: UpdateContentsUseCase,
    private val updateWorkUseCase: UpdateWorkUseCase
) : BaseViewModel() {
    private var isWorkChanged: Boolean = false
    private var idWorkFocus: Long = 0
    private var mWorkPosition: Int = -1

    private val idGroup: MediatorLiveData<Long> = MediatorLiveData<Long>()

    private val listWorkDataLocal: LiveData<List<WorkDataItemView>> = idGroup.switchMapLiveData {
        fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup.value ?: return@switchMapLiveData)).map { it ->
            it.map { it ->
                WorkDataItemView(it, it.listContent.map { ContentDataItemView(it, false) }
                        as ArrayList<ContentDataItemView>)
            }
        }.collect {
            emit(it)
        }
    }

    val listWorkData = listWorkDataLocal.switchMapLiveDataEmit {
        if (mWorkPosition != -1 && isWorkChanged) {
            it[mWorkPosition].listContent.add(
                ContentDataItemView(
                    ContentDataEntity(
                        System.currentTimeMillis(), "", it[mWorkPosition].work.id
                    ), true
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

    fun insertContentToWork(content: ContentDataEntity, work: WorkDataEntity, workPosition: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            mWorkPosition = workPosition
            isWorkChanged = if (content.name.isNotBlank()) {
                idWorkFocus = content.idOwnerWork
                insertContentUseCase.invoke(InsertContentUseCase.Param(content))
                true
            } else {
                false
            }
        }
    }

    fun insertWorksObject(works: List<WorkDataEntity>) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            insertListWorkUseCase.invoke(InsertListWorkUseCase.Param(works))
        }
    }

    fun insertWorkInCurrent(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            insertWorkUseCase.invoke(
                InsertWorkUseCase.Param(
                    WorkDataEntity(
                        System.currentTimeMillis(),
                        name, listWorkData.value?.get(0)?.work?.groupId ?: 0, arrayListOf()
                    )
                )
            )
        }
    }

    fun updateWork(work: WorkDataEntity, workPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            mWorkPosition = workPosition
            isWorkChanged = true
            updateContentToWork(work, workPosition)
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(work))
        }
    }

    suspend fun updateContentToWork(work: WorkDataEntity, workPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            mWorkPosition = workPosition
            isWorkChanged = true
            updateContentsUseCase.invoke(UpdateContentsUseCase.Param(work.listContent))
        }
    }

    fun handlerCheckItem(content: ContentDataEntity) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            insertContentUseCase.invoke(InsertContentUseCase.Param(content))
        }
    }

    data class ContentDataItemView(var content: ContentDataEntity, var isFocus: Boolean)

    data class WorkDataItemView(
        val work: WorkDataEntity,
        var listContent: MutableList<ContentDataItemView>
    )
}
