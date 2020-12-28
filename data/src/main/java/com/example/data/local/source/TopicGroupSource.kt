package com.example.data.local.source

import com.example.data.local.model.TopicDataModel

interface TopicGroupSource {
    suspend fun fetchAll() : List<TopicDataModel>
}