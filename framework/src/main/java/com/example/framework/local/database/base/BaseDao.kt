package com.example.framework.local.database.base

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDatas(vararg datas: T)

    @Update
    suspend fun updateDatas(vararg datas: T)

    @Delete
    suspend fun deleteDatas(vararg datas: T)
}