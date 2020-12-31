package com.example.data.local.source

import com.example.data.local.model.TopicDataModel

interface TopicGroupSource : BaseSource<TopicDataModel> {
    suspend fun fetchAll(): List<TopicDataModel>
}