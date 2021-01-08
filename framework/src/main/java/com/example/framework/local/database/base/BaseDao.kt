package com.example.framework.local.database.base

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserts(vararg datas: T)

    @Transaction
    @Update
    suspend fun updateData(data: T)

    @Update
    suspend fun updateDatas(vararg datas: T)

    @Delete
    suspend fun deleteDatas(vararg datas: T)
}