package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.framework.model.WorkFoTopic

@Dao
interface LocalWorkFromTopicDao {
    @Query("SELECT * FROM WorkFoTopic")
    suspend fun fetchWorkFromTopicData(): List<WorkFoTopic>
}