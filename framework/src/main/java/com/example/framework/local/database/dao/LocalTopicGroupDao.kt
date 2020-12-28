package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.framework.model.TopicGroup

@Dao
interface LocalTopicGroupDao {
    @Query("SELECT * FROM TopicGroup")
    suspend fun fetchTopicGroupData() : List<TopicGroup>
}
