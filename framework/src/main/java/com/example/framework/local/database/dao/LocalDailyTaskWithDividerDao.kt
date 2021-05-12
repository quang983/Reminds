package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.framework.local.database.base.BaseDao
import com.example.framework.local.database.model.DailyTask
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDailyTaskWithDividerDao : BaseDao<DailyTask> {
    @Transaction
    @Query("SELECT * FROM DailyTask")
    fun getAllDataFlow(): Flow<List<DailyTask>>
}