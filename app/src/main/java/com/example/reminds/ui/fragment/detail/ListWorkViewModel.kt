package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.content.InsertContentUseCase
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.domain.usecase.db.workintopic.InsertWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase,
    private val insertContentUseCase: InsertContentUseCase,
    private val insertWorkUseCase: InsertWorkUseCase
) : BaseViewModel() {
    private var isInsert: Boolean = false
    val listWorkData: LiveData<List<WorkDataEntity>> = MutableLiveData()

    fun getListWork(idGroup: Long) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup)).collect { it ->
                listWorkData.postValue(it.onEach {
                    it.listContent.add(ContentDataEntity(if (!isInsert) 0 else System.currentTimeMillis(), "", it.id))
                })
            }
        }
    }

    fun insertContentToWork(content: ContentDataEntity, work: WorkDataEntity, workPosition: Int) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            isInsert = if (content.name.isNotBlank()) {
                insertContentUseCase.invoke(InsertContentUseCase.Param(content))
                true
            } else {
                false
            }
        }
    }

    fun insertWork(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
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

}