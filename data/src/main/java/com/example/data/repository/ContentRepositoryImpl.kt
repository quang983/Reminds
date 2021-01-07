package com.example.data.repository

import com.example.common.base.model.ContentDataEntity
import com.example.data.local.source.ContentFromWorkSource
import com.example.domain.repository.ContentRepository
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    private val source: ContentFromWorkSource
) : ContentRepository {
    override suspend fun insertData(data: ContentDataEntity) : Long{
       return source.insert(ContentDataEntity(data.id, data.name, data.idOwnerWork,data.isChecked))
    }

    override suspend fun insertDatas(datas: List<ContentDataEntity>) {
    }

    override suspend fun updateDatas(datas: List<ContentDataEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDatas(datas: List<ContentDataEntity>) {
        TODO("Not yet implemented")
    }
}