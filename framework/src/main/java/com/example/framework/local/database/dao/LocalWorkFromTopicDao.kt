package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.framework.local.database.model.TopicGroupWithWorks
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalWorkFromTopicDao {
    @Transaction
    @Query("SELECT * FROM WorkFoTopic where id LIKE :id")
    fun fetchWorkFromTopicData(id: Long): Flow<TopicGroupWithWorks>
}
