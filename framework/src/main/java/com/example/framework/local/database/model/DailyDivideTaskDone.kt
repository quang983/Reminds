package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyDivideTaskDone(
    @PrimaryKey
    var id: Long,
    var idGroup: Long,
    var name: String,
    var doneTime: Long,
    var remainingTime: Long
) /*: BaseConverter<DailyDivideTaskDone, DailyDivideTaskDoneEntity> {
    override fun convert(): DailyDivideTaskDoneEntity =
        DailyDivideTaskDoneEntity(id, idGroup, name, doneTime, remainingTime)

    override fun copy(data: DailyDivideTaskDoneEntity): DailyDivideTaskDone {
        id = data.id
        idGroup = data.idGroup
        name = data.name
        doneTime = data.doneTime
        remainingTime = data.remainingTime
        return this
    }
}*/