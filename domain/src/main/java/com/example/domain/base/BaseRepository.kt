package com.example.domain.base

interface BaseRepository<T> {
    suspend fun insertData(data: T): Long

    suspend fun insertDatas(datas: List<T>)

    suspend fun updateData(data: T)

    suspend fun updateDatas(datas: List<T>)

    suspend fun deleteDatas(datas: List<T>)
}