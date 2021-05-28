package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.framework.local.database.base.BaseDao
import com.example.framework.local.database.model.DailyTask
import com.example.framework.local.database.model.DailyTaskWithDivider
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDailyTaskWithDividerDao : BaseDao<DailyTask> {
    @Transaction
    @Query("SELECT * FROM DailyTask")
    fun getAllDataFlow(): Flow<List<DailyTaskWithDivider>>

    @Query("SELECT * FROM DailyTask  where id=:id")
    fun getDetailById(id: Long): Flow<DailyTaskWithDivider>
}