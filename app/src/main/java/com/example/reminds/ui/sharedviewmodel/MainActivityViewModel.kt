package com.example.reminds.ui.sharedviewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.domain.usecase.db.workintopic.InsertWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivityViewModel @ViewModelInject constructor(
    private val insertTopicUseCase: InsertTopicUseCase,
    private val insertWorkUseCase: InsertWorkUseCase
) : BaseViewModel() {
    val isKeyboardShow: LiveData<Boolean> = MutableLiveData(false)

    fun addFirstTopic() {
        GlobalScope.launch(handler + Dispatchers.IO) {
            val data = TopicGroupEntity(1, "Hôm nay")
            insertTopicUseCase.invoke(InsertTopicUseCase.Param(data)).let {
                insertWorkUseCase.invoke(
                    InsertWorkUseCase.Param(
                        WorkDataEntity(
                            id = System.currentTimeMillis(),
                            name = "Cơ bản",
                            groupId = it,
                            listContent = mutableListOf()
                        )
                    )
                )
            }
        }
    }
}