package com.example.data.local.source

interface BaseSource<T> {
    suspend fun insert(data: T) : Long

    suspend fun inserts(datas: T)

    suspend fun update(datas: T)
    suspend fun updates(datas: List<T>)

    suspend fun delete(datas: T)
    suspend fun deletes(datas: List<T>)
}