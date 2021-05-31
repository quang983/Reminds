package com.example.reminds.ui.fragment.createtopic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.TopicGroupEntity
import com.example.domain.usecase.db.topic.FindTopicByIdUseCase
import com.example.domain.usecase.db.topic.InsertTopicUseCase
import com.example.reminds.R
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTopicBSViewModel @Inject constructor(
    private val insertTopicUseCase: InsertTopicUseCase,
    private val findTopicByIdUseCase: FindTopicByIdUseCase
) : BaseViewModel() {
    private val _list: IntArray = intArrayOf(
        R.drawable.image_5, R.drawable.image_6, R.drawable.image_8, R.drawable.image_12,
        R.drawable.image_29, R.drawable.image_32, R.drawable.image_34, R.drawable.image_35, R.drawable.image_38, R.drawable.image_111,
        R.drawable.image_112, R.drawable.image_117, R.drawable.image_177, R.drawable.image_185, R.drawable.image_187, R.drawable.image_229,
        R.drawable.image_256, R.drawable.image_258, R.drawable.image_259, R.drawable.image_261, R.drawable.image_263, R.drawable.image_265,
        R.drawable.image_266, R.drawable.image_270, R.drawable.image_272, R.drawable.image_307, R.drawable.image_386, R.drawable.image_877,
        R.drawable.image_879, R.drawable.image_1094, R.drawable.image_1098, R.drawable.image_1109, R.drawable.image_1110, R.drawable.image_1123,
        R.drawable.image_1224, R.drawable.image_1224, R.drawable.image_1226, R.drawable.image_1347, R.drawable.image_1454, R.drawable.image_1547,
        R.drawable.image_1569, R.drawable.image_1693, R.drawable.image_1701, R.drawable.image_1729
    )

    val listIconLiveData: LiveData<IntArray> = MutableLiveData()

    var topicTemp = TopicGroupEntity(System.currentTimeMillis(), "", typeTopic = TopicGroupEntity.TYPE_NORMAL, iconResource = R.drawable.image_5)

    val idTopicGroup: LiveData<Long> = MutableLiveData()

    val topicGroup: LiveData<TopicGroupEntity> = idTopicGroup.switchMapLiveData {
        findTopicByIdUseCase.invoke(FindTopicByIdUseCase.Param(it))?.let { topic ->
            topicTemp = topic.copy()
            emit(topic)
        }
    }

    fun findPositionIcon(icon: Int): Int {
        return _list.indexOf(icon)

    }

    fun postIdGroup(idTopic: Long) {
        idTopicGroup.postValue(idTopic)
    }

    init {
        listIconLiveData.postValue(_list)
    }

    fun setNameTopicTemp(name: String) {
        topicTemp.name = name
    }

    fun setIconResourceTopicTemp(resource: Int) {
        topicTemp.iconResource = resource
    }

    fun insertTopicToDatabase() = viewModelScope.launch(handler + Dispatchers.IO) {
        kotlin.runCatching {
            insertTopicUseCase.invoke(InsertTopicUseCase.Param(topicTemp))
        }
    }
}