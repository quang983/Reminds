package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.framework.local.database.base.BaseDao
import com.example.framework.local.database.model.TopicGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTopicGroupDao : BaseDao<TopicGroup> {
    @Query("SELECT * FROM TopicGroup")
    fun fetchTopicGroupDataFlow(): Flow<List<TopicGroup>>

    @Query("SELECT * FROM TopicGroup")
    fun fetchTopicGroupData(): List<TopicGroup>

    @Query("SELECT * FROM TopicGroup  LIMIT 1")
    fun fetchTopicTodayData(): TopicGroup?
}
