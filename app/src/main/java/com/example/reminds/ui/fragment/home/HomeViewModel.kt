package com.example.reminds.ui.fragment.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.example.domain.model.TopicGroupEntity
import com.example.domain.repository.BaseUseCase
import com.example.domain.usecase.db.FetchTopicUseCase
import net.citigo.kiotviet.pos.fnb.ui.viewmodels.BaseViewModel

class HomeViewModel @ViewModelInject constructor(fetchTopicUseCase: FetchTopicUseCase) : BaseViewModel() {
    val topicData : LiveData<List<TopicGroupEntity>> = liveDataEmit {
        fetchTopicUseCase.invoke(BaseUseCase.Param())
    }
}