package com.example.data.local.source

interface BaseSource<T> {
    suspend fun inserts(datas: List<T>)

    suspend fun updates(datas: List<T>)

    suspend fun deletes(datas: List<T>)
}