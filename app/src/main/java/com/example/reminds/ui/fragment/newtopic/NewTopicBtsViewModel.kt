package com.example.reminds.ui.fragment.newtopic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.domain.usecase.db.workintopic.InsertWorkUseCase
import com.example.reminds.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewTopicBtsViewModel @ViewModelInject constructor(
    private val insertTopicUseCase: InsertTopicUseCase,
    private val insertWorkUseCase: InsertWorkUseCase
) : BaseViewModel() {
    fun insertTopic(name: String) {
        viewModelScope.launch(handler + Dispatchers.IO) {
            val data = TopicGroupEntity(System.currentTimeMillis(), name)
            kotlin.runCatching {
                val idTopic = insertTopicUseCase.invoke(InsertTopicUseCase.Param(data))
                insertWorkUseCase.invoke(
                    InsertWorkUseCase.Param(
                        WorkDataEntity(
                            id = System.currentTimeMillis(),
                            name = "Chung",
                            groupId = idTopic,
                            listContent = mutableListOf(),
                            doneAll = false
                        )
                    )
                )
            }
        }
    }
}
