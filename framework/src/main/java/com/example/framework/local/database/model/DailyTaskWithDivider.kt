package com.example.framework.local.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.framework.local.database.convert.BaseConverter

data class DailyTaskWithDivider(
    @Embedded var dailyTask: DailyTask,
    @Relation(
        entity = DailyDivideTaskDone::class,
        parentColumn = "id",
        entityColumn = "idGroup"
    )
    var listDaily: List<DailyDivideTaskDone>
) : BaseConverter<DailyTaskWithDivider, DailyTaskWithDividerEntity> {
    override fun convert(): DailyTaskWithDividerEntity =
        DailyTaskWithDividerEntity(dailyTask.convert(), listDaily.map { it.convert() })

    override fun copy(data: DailyTaskWithDividerEntity): DailyTaskWithDivider {
        dailyTask = DailyTask(data.dailyTask.id, data.dailyTask.name, data.dailyTask.createTime, data.dailyTask.endTime)
        (listDaily as? ArrayList)?.clear()
        (listDaily as? ArrayList)?.addAll(data.dailyList.map { DailyDivideTaskDone(it.id, it.idGroup, it.name, it.doneTime, it.remainingTime) })
        return this
    }
}
