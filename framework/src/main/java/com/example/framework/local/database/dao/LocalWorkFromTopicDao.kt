package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.framework.local.database.base.BaseDao
import com.example.framework.local.database.model.TopicGroupWithWorks
import com.example.framework.local.database.model.WorkFoTopic
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalWorkFromTopicDao : BaseDao<WorkFoTopic> {
    @Transaction
    @Query("SELECT * FROM TopicGroup where idTopic=:id")
    fun fetchWorkFromTopicDataFlow(id: Long): Flow<TopicGroupWithWorks>

    @Transaction
    @Query("SELECT * FROM WorkFoTopic where doneAll=:doneAll")
    fun getAllWorkDependStateFlow(doneAll: Boolean): Flow<List<WorkFoTopic>>

    @Transaction
    @Query("SELECT * FROM WorkFoTopic")
    fun getAllWorkFlow(): Flow<List<WorkFoTopic>>

    @Transaction
    @Query("SELECT * FROM TopicGroup where idTopic=:id")
    fun fetchWorkFromTopicData(id: Long): TopicGroupWithWorks?

    @Query("SELECT * FROM WorkFoTopic")
    fun fetchWork(): List<WorkFoTopic>

    @Query("SELECT * FROM WorkFoTopic where id=:id")
    fun findById(id: Long): WorkFoTopic?

}
