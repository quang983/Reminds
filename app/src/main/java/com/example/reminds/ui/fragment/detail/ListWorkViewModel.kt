package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.content.InsertContentUseCase
import com.example.domain.usecase.db.content.UpdateContentsUseCase
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.domain.usecase.db.workintopic.InsertListWorkUseCase
import com.example.domain.usecase.db.workintopic.InsertWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertContentUseCase: InsertContentUseCase,
    private val insertWorkUseCase: InsertWorkUseCase,
    private val insertListWorkUseCase: InsertListWorkUseCase,
    private val updateContentsUseCase: UpdateContentsUseCase
) : BaseViewModel() {
    private var isWorkChanged: Boolean = false
    private var idWorkFocus: Long = 0
    private var mWorkPosition: Int = -1
    val listWorkData: LiveData<List<WorkDataItemView>> = MutableLiveData()

    fun getListWork(idGroup: Long) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup)).collect { it ->
                val listMap = it.map { it ->
                    WorkDataItemView(it, it.listContent.map { ContentDataItemView(it, false) }
                            as ArrayList<ContentDataItemView>)
                }.apply {
                    if (mWorkPosition != -1 && isWorkChanged) {
                        this[mWorkPosition].listContent.add(
                            ContentDataItemView(
                                ContentDataEntity(
                                    System.currentTimeMillis(), "", this[mWorkPosition].work.id
                                ), true
                            )
                        )
                    }
                }
                listWorkData.postValue(listMap)
                setDefaultValue()
            }
        }
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
            /*insertWorkUseCase.invoke(
                InsertWorkUseCase.Param(
                    work
                )
            )*/
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
