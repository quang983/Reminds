package com.example.framework.source

import com.example.data.local.model.TopicDataModel
import com.example.data.local.source.TopicGroupSource
import com.example.framework.local.database.dao.LocalTopicGroupDao
import javax.inject.Inject

class TopicGroupSourceImpl @Inject constructor(
    private val dao: LocalTopicGroupDao
) : TopicGroupSource {
    override suspend fun fetchAll(): List<TopicDataModel> {
        return dao.fetchTopicGroupData().map {
            TopicDataModel(
                id = it.id, name = it.name
            )
        }
    }
}