package com.example.data.local.source

import com.example.common.base.model.WorkDataEntity

interface WorkFromTopicSource {
    suspend fun fetchAll(): List<WorkDataEntity>
}