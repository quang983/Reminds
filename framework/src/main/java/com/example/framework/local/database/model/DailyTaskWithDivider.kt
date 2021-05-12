package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyTaskWithDivider(@PrimaryKey val a : String)
/*: BaseConverter<DailyTaskWithDivider, DailyTaskWithDividerEntity> {
    override fun convert(): DailyTaskWithDividerEntity =
        DailyTaskWithDividerEntity(dailyTask.convert(), listDaily.map { it.convert() })

    override fun copy(data: DailyTaskWithDividerEntity): DailyTaskWithDivider {
        dailyTask =DailyTask(data.dailyTask.id,data.dailyTask.name,data.dailyTask.createTime,data.dailyTask.endTime)
        listDaily.clear()
        listDaily.addAll(data.dailyList.map { DailyDivideTaskDone(it.id, it.idGroup, it.name, it.doneTime, it.remainingTime) })
        return this
    }
}
*/