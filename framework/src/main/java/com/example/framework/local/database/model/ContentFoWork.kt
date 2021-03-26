package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.model.ContentDataEntity
import com.example.framework.local.database.convert.BaseConverter

@Entity
data class ContentFoWork(
    @PrimaryKey(autoGenerate = true)
    var idContent: Long = 0,
    var name: String = "",
    var idOwnerWork: Long = 0,
    var hashTag: Boolean = false,
    var timer: Long = -1,
    var timerStr: String = "",
    var isCheckDone: Boolean = false
) : BaseConverter<ContentFoWork, ContentDataEntity> {
    override fun convert() = ContentDataEntity(idContent, name, idOwnerWork, hashTag, timer, isCheckDone)

    override fun copy(data: ContentDataEntity): ContentFoWork {
        idContent = data.id
        name = data.name
        idOwnerWork = data.idOwnerWork
        hashTag = data.hashTag
        timer = data.timer
        isCheckDone = data.isCheckDone
        return this
    }

}
