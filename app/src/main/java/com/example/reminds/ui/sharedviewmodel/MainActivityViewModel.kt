package com.example.reminds.ui.sharedviewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.TopicGroupEntity
import com.example.common.base.model.TopicGroupEntity.Companion.REMOVE_DONE_WORKS
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

    val showAdsMobile: LiveData<Boolean> = MutableLiveData(false)

    val notifyDataInsert: LiveData<AlarmNotificationEntity> = MutableLiveData()

    fun setNotifyDataInsert(alarm: AlarmNotificationEntity) = GlobalScope.launch(Dispatchers.IO + handler) {
        notifyDataInsert.postValue(alarm)
    }

    fun addFirstTopic(topic: String) {
        GlobalScope.launch(handler + Dispatchers.IO) {
            val data = TopicGroupEntity(1, "Today", false, REMOVE_DONE_WORKS)
            insertTopicUseCase.invoke(InsertTopicUseCase.Param(data)).let {
                insertWorkUseCase.invoke(
                    InsertWorkUseCase.Param(
                        WorkDataEntity(
                            id = System.currentTimeMillis(),
                            name = topic,
                            groupId = it,
                            listContent = mutableListOf(),
                            doneAll = false,
                            isShowContents = false
                        )
                    )
                )
            }
        }
    }


}