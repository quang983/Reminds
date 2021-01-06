package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.framework.local.database.base.BaseDao
import com.example.framework.local.database.model.TopicGroupWithWorks
import com.example.framework.local.database.model.WorkFoTopic
import com.example.framework.local.database.model.WorkWithContent
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalWorkFromTopicDao : BaseDao<WorkFoTopic> {
    @Transaction
    @Query("SELECT * FROM TopicGroup where idTopic=:id")
    fun fetchWorkFromTopicData(id: Long): Flow<TopicGroupWithWorks>

    @Query("SELECT * FROM WorkFoTopic")
    fun fetchWork(): List<WorkFoTopic>
}
