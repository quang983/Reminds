package com.example.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.framework.local.database.base.BaseDao
import com.example.framework.local.database.model.DailyTaskWithDivider
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalDailyTaskWithDividerDao : BaseDao<DailyTaskWithDivider> {
    @Transaction
    @Query("SELECT * FROM DailyTaskWithDivider")
    suspend fun getAllDataFlow(): Flow<List<DailyTaskWithDivider>>
}