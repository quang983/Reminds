package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.model.daily.DailyDivideTaskDoneEntity
import com.example.framework.local.database.convert.BaseConverter

@Entity
data class DailyDivideTaskDone(
    @PrimaryKey
    var id: Long,
    var idGroup: Long,
    var name: String,
    var doneTime: Long
) : BaseConverter<DailyDivideTaskDone, DailyDivideTaskDoneEntity> {
    override fun convert(): DailyDivideTaskDoneEntity =
        DailyDivideTaskDoneEntity(id, idGroup, name, doneTime)

    override fun copy(data: DailyDivideTaskDoneEntity): DailyDivideTaskDone {
        id = data.id
        idGroup = data.idGroup
        name = data.name
        doneTime = data.doneTime
        return this
    }
}