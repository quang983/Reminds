package com.example.framework.source

import com.example.common.base.model.ContentDataEntity
import com.example.data.local.source.ContentFromWorkSource
import com.example.framework.local.database.dao.LocalContentFromWorkDao
import com.example.framework.local.database.model.ContentFoWork
import javax.inject.Inject

class ContentFromWorkSourceImpl @Inject constructor(
    private val dao: LocalContentFromWorkDao
) : ContentFromWorkSource {
    override suspend fun insert(data: ContentDataEntity): Long {
        return dao.insert(ContentFoWork(data.id, data.name, data.idOwnerWork))
    }

    override suspend fun inserts(datas: List<ContentDataEntity>) {
    }

    override suspend fun update(data: ContentDataEntity) {
    }

    override suspend fun updates(datas: List<ContentDataEntity>) {
        dao.updateDatas(*datas.map { ContentFoWork(it.id, it.name, it.idOwnerWork) }.toTypedArray())
    }

    override suspend fun delete(datas: ContentDataEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletes(datas: List<ContentDataEntity>) {
        dao.deleteDatas(*datas.map { ContentFoWork(it.id, it.name, it.idOwnerWork) }.toTypedArray())
    }
}