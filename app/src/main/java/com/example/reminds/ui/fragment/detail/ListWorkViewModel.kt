package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.content.InsertContentUseCase
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.domain.usecase.db.workintopic.InsertListWorkUseCase
import com.example.domain.usecase.db.workintopic.InsertWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertContentUseCase: InsertContentUseCase,
    private val insertWorkUseCase: InsertWorkUseCase,
    private val insertListWorkUseCase: InsertListWorkUseCase
) : BaseViewModel() {
    private var isInsert: Boolean = false
    private var idWorkFocus: Long = 0
    val listWorkData: LiveData<List<WorkDataItemView>> = MutableLiveData()

    fun getListWork(idGroup: Long) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup)).collect { it ->
                val listMap = it.map { it ->
                    WorkDataItemView(it, it.listContent.map { ContentDataItemView(it, false) }
                            as ArrayList<ContentDataItemView>)
                }.onEach {
                    var isFocus = false
                    if (it.work.id == idWorkFocus && isInsert) {
                        isFocus = true
                    }
                    it.listContent.add(
                        ContentDataItemView(
                            ContentDataEntity(
                                if (!isInsert) 0
                                else System.currentTimeMillis(), "", it.work.id
                            ), isFocus
                        )
                    )
                }
                listWorkData.postValue(listMap)
            }
        }
    }

    fun insertContentToWork(content: ContentDataEntity, work: WorkDataEntity, workPosition: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            isInsert = if (content.name.isNotBlank()) {
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

    fun insertWork(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
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

    fun handlerCheckItem(isChecked: Boolean, content: ContentDataEntity) {
        viewModelScope.launch {
            if (isChecked) {
                launch {
                    delay(1000L)
                    content.isChecked = true
                    insertContentUseCase.invoke(InsertContentUseCase.Param(content))
                    this.cancel()
                }
            } else {
                content.isChecked = false
                insertContentUseCase.invoke(InsertContentUseCase.Param(content))
            }
        }
    }

    data class ContentDataItemView(var content: ContentDataEntity, var isFocus: Boolean)

    data class WorkDataItemView(
        val work: WorkDataEntity,
        var listContent: ArrayList<ContentDataItemView>
    )
}