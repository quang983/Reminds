package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.common.base.model.WorkDataEntity
import com.example.framework.local.database.convert.BaseConverter
import com.example.framework.local.database.convert.DataConverter

@Entity
class WorkFoTopic : BaseConverter<WorkFoTopic, WorkDataEntity> {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var name: String = ""
    var idOwnerGroup: Long = 0

    @TypeConverters(DataConverter::class)
    var listContent: MutableList<ContentFoWork> = mutableListOf()
    var doneAll: Boolean = false
    var isShowContents: Boolean = false
    var hashTag: Boolean = false
    var timerReminder: Long = -1
    var createTime: Long = id
    var stt: Int = 0
    var description: String = ""

    override fun convert() = WorkDataEntity(
        id, name, idOwnerGroup, listContent.map { it.convert() }.toMutableList(),
        doneAll, isShowContents, hashTag, timerReminder, createTime, stt, description
    )

    override fun copy(data: WorkDataEntity): WorkFoTopic {
        id = data.id
        name = data.name
        idOwnerGroup = data.groupId
        listContent = data.listContent.map { ContentFoWork().copy(it) }.toMutableList()
        doneAll = data.doneAll
        isShowContents = data.isShowContents
        hashTag = data.hashTag
        timerReminder = data.timerReminder
        createTime = data.createTime
        stt = data.stt
        description = data.description
        return this
    }
}
