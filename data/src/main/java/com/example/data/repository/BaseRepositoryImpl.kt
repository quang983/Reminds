package com.example.data.repository

import com.example.data.local.source.BaseSource
import com.example.domain.base.BaseRepository

open class BaseRepositoryImpl<T>(val source: BaseSource<T>) : BaseRepository<T> {
    override suspend fun insertData(data: T): Long {
        return source.insert(data)
    }

    override suspend fun insertDatas(datas: List<T>) {
        source.inserts(datas)
    }

    override suspend fun updateData(data: T) {
        source.update(data)
    }

    override suspend fun updateDatas(datas: List<T>) {
        source.updates(datas)
    }

    override suspend fun deleteDatas(datas: List<T>) {
        source.deletes(datas)
    }
}