package com.example.reminds.ui.fragment.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.FetchWorksUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListWorkViewModel @ViewModelInject constructor(
    private val fetchWorksUseCase: FetchWorksUseCase
) : BaseViewModel() {
    val listWorkData: LiveData<List<WorkDataEntity>> = MutableLiveData()
//    val listContentData: LiveData<List<ContentDataEntity>> = MutableLiveData()

    fun getListWork(idGroup: Long) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            fetchWorksUseCase.invoke(FetchWorksUseCase.Param(idGroup)).collect { it ->
                listWorkData.postValue(it.onEach {
                    it.listContent.add(ContentDataEntity(0, ""))
                })
            }
        }
    }

    fun insertContentToWork(content: ContentDataEntity, work: WorkDataEntity, workPosition: Int) {
        //insert content
        val list = listWorkData.value?.map {
            WorkDataEntity(
                it.id,
                it.name,
                it.groupId,
                it.listContent.map {
                    ContentDataEntity(it.id, it.name, it.idOwnerWork)
                }.toMutableList() as ArrayList<ContentDataEntity>
            )
        }?.apply {
            val list = this[workPosition].listContent

            list[list.indexOf(content)].name = content.name

            list.add(ContentDataEntity(System.currentTimeMillis(), ""))
        }
        listWorkData.postValue(list as List<WorkDataEntity>)
    }
}