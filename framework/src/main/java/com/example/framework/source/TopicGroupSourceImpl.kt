package com.example.framework.source

import com.example.data.local.model.TopicDataModel
import com.example.data.local.source.TopicGroupSource
import com.example.framework.local.database.dao.LocalTopicGroupDao
import com.example.framework.model.TopicGroup
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

    override suspend fun inserts(datas: List<TopicDataModel>) {
        dao.insertDatas(*datas.map { TopicGroup().toModel(it) }.toTypedArray())
    }

    override suspend fun updates(datas: List<TopicDataModel>) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<TopicDataModel>) {
        TODO("Not yet implemented")
    }
}